package com.kh.spring.videoCall.model.service;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.challenge.model.dao.ChallengeDao;
import com.kh.spring.challenge.model.vo.ConnectByUuid;
import com.kh.spring.challenge.model.vo.LoginUserAndChal;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.challenge.ChallengeFix;
import com.kh.spring.util.common.Regexp;
import com.kh.spring.util.common.UuidUtil;
import com.kh.spring.util.videoCall.VideoCallValidator;
import com.kh.spring.videoCall.model.dao.VideoCallDao;
import com.kh.spring.videoCall.model.vo.Challenge;
import com.kh.spring.videoCall.model.vo.VideoCallRequest;
import com.kh.spring.videoCall.model.vo.VideoCallResponse;

import jakarta.servlet.http.HttpSession;

@Service
public class VideoCallServiceImpl implements VideoCallService{

	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private VideoCallDao dao;
	@Autowired
	private ChallengeDao chalDao;
	@Autowired
	private DailyService dailyService;
	
	//비동기 - 새로운 방 생성
	@Override
	@Transactional
	public ResponseEntity<String> createRoom(HttpSession session, VideoCallRequest vcr) throws Exception {
		if(!VideoCallValidator.createRoom(vcr)) return ResponseEntity.badRequest().build();
		User loginUser = (User)session.getAttribute("loginUser");
		vcr.setStatus("N");
		vcr.setRoomName(vcr.getRoomName().trim());
		
		//해당 챌린지 생성자인지 검증
		LoginUserAndChal lac = LoginUserAndChal.builder()
				.userNo(loginUser.getUserNo())
				.chalNo(vcr.getChalNo())
				.build();
		int loginUserIsWriter = chalDao.loginUserIsWriter(sqlSession,lac);
		if(!(loginUserIsWriter>0)) return ResponseEntity.badRequest().build();
		
		//uuid생성하기
		Map<String, Object> createdUuid = UuidUtil.createUUID();
		vcr.setRoomUuid((byte[])createdUuid.get("uuidRaw"));
		
        //DB에 방 저장
        int result = dao.insertRoom(sqlSession, vcr);
        
        if(result==0) return ResponseEntity.status(500).build();
        
		return ResponseEntity.ok().build();
	}

	//비동기 - 생성한 방 조회
	@Override
	public ResponseEntity<List<VideoCallResponse>> createdRoom(HttpSession session) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		
		List<VideoCallResponse> result = dao.createdRoom(sqlSession, loginUser.getUserNo());
		
		//비었으면 404
		if (result == null || result.isEmpty())
			return ResponseEntity.notFound().build();	
		
		//소독
		vcResponseSanitizer(result);
		
		return ResponseEntity.ok(result);
	}
	
	//비동기 - 참여한 방 조회
	@Override
	public ResponseEntity<List<VideoCallResponse>> invitedRoom(HttpSession session) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		
		List<VideoCallResponse> result = dao.invitedRoom(sqlSession, loginUser.getUserNo());
		
		//비었으면 404
		if (result == null || result.isEmpty())
			return ResponseEntity.notFound().build();	
		
		//소독
		vcResponseSanitizer(result);
		
		return ResponseEntity.ok(result);
	}

	//비동기 - 생성하지 않은 방 조회
	@Override
	public ResponseEntity<List<Challenge>> notCreatedRoom(HttpSession session) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		
		List<Challenge> result = dao.notCreatedRoom(sqlSession, loginUser.getUserNo());
		
		//비었으면 404
		if (result == null || result.isEmpty())
			return ResponseEntity.notFound().build();
		
		//소독
		challengeSanitizer(result);
		
		return ResponseEntity.ok(result);
	}
	
	//비동기 - 방 열기
	@Override
	@Transactional
	public ResponseEntity<String> openRoom(HttpSession session, String roomUuidStr) throws Exception{
		User loginUser = (User)session.getAttribute("loginUser");
		byte[] roomUuid = UuidUtil.strToByteArr(roomUuidStr);
		ConnectByUuid uuidAndUserNo = ConnectByUuid.builder()
				.uuid(roomUuid)
				.refNo(loginUser.getUserNo())
				.build();
		
		//작성자가 해당 방의 주인인지 확인하고 열기
		int openRoom = dao.openRoom(sqlSession, uuidAndUserNo);
		if(!(openRoom>0)) return ResponseEntity.badRequest().build();
		
		//Daily.co에서 방 만들기
		String ownerToken = dailyService.createMeetingToken(roomUuidStr, loginUser, "O").block();
		if(ownerToken==null) return ResponseEntity.status(500).build();
		
		//Daily.co에서 방 열기
		String createdRoom = dailyService.createRoom(roomUuidStr);
		if(createdRoom==null) return ResponseEntity.status(500).build();
		
		//방 접근 url생성
		String accessUrl = createdRoom + "?t=" + ownerToken;
		
		return ResponseEntity.ok(accessUrl);
	}
	
	//비동기 - 방 참여하기
	@Override
	public ResponseEntity<String> participateRoom(HttpSession session, String roomUuidStr) throws Exception{
		User loginUser = (User)session.getAttribute("loginUser");
		byte[] roomUuid = UuidUtil.strToByteArr(roomUuidStr);
		ConnectByUuid uuidAndUserNo = ConnectByUuid.builder()
				.uuid(roomUuid)
				.refNo(loginUser.getUserNo())
				.build();
		
		//작성자가 해당 방에 참여할 권한이 있니?
		int isParticipant = dao.isParticipant(sqlSession, uuidAndUserNo);
		if(!(isParticipant>0)) return ResponseEntity.badRequest().build();
		
		//----------------------------------------------------
		//Daily.co에서 방 만들기
		String ownerToken = dailyService.createMeetingToken(roomUuidStr, loginUser, "P").block();
		if(ownerToken==null) return ResponseEntity.status(500).build();
		
		//Daily.co에서 방 열기
		String createdRoom = dailyService.createRoom(roomUuidStr);
		if(createdRoom==null) return ResponseEntity.status(500).build();
		
		//방 접근 url생성
		String accessUrl = createdRoom + "?t=" + ownerToken;
		
		return ResponseEntity.ok(accessUrl);
	}
	
	
	
	
	
	//챌린지 조회 소독
	@Override
	public void challengeSanitizer(List<Challenge> challenge) throws Exception{
		for(Challenge chal : challenge) {
			// 제목이 표시 제한 초과일 경우
			if (chal.getTitle().length() > Regexp.TITLE_SHOW_LIMIT) {
				chal.setTitle(chal.getTitle().substring(0, Regexp.TITLE_SHOW_LIMIT) + "⋯");
			}
			
			//내용에 이미지 태그 등이 있을 경우
			chal.setContent(ChallengeFix.deleteContentTag(chal.getContent()));
			
			// 내용이 표시 제한 초과일 경우
			if (chal.getContent().length() > Regexp.CONTENT_SHOW_LIMIT) {
				chal.setContent(chal.getContent().substring(0, Regexp.CONTENT_SHOW_LIMIT) + "⋯");
			}
		}
	}
	
	//VideoCallResponse 소독
	@Override
	public void vcResponseSanitizer(List<VideoCallResponse> vcr) throws Exception{
		for(VideoCallResponse chal : vcr) {
			// 제목이 표시 제한 초과일 경우
			if (chal.getTitle().length() > Regexp.TITLE_SHOW_LIMIT) {
				chal.setTitle(chal.getTitle().substring(0, Regexp.TITLE_SHOW_LIMIT) + "⋯");
			}
			
			//내용에 이미지 태그 등이 있을 경우
			chal.setContent(ChallengeFix.deleteContentTag(chal.getContent()));
			
			// 내용이 표시 제한 초과일 경우
			if (chal.getContent().length() > Regexp.CONTENT_SHOW_LIMIT) {
				chal.setContent(chal.getContent().substring(0, Regexp.CONTENT_SHOW_LIMIT) + "⋯");
			}
		}
	}
}
