package com.kh.spring.challenge.model.service;

import java.util.Base64;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kh.spring.challenge.model.dao.ProfileDao;
import com.kh.spring.challenge.model.vo.ChallengeResponse;
import com.kh.spring.challenge.model.vo.ProfileRequest;
import com.kh.spring.challenge.model.vo.ProfileResponse;
import com.kh.spring.challenge.model.vo.SearchMyChallenge;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.challenge.ChallengeFix;
import com.kh.spring.util.challenge.ProfileValidator;
import com.kh.spring.util.common.BinaryAndBase64;

import jakarta.servlet.http.HttpSession;


@Service
public class ProfileServiceImpl implements ProfileService{
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private ProfileDao dao;
	
	//내 프로필 조회
	@Override
	public int selectMyProfile(HttpSession session) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		
		//로그인조차 하지 않았다니 어흐흑 마이깟
		if(loginUser==null) return 2;
		
		ProfileResponse userProfile = dao.selectMyProfile(sqlSession,loginUser.getUserNo());
		
		//신루틴의 긍휼한 축복을 받고도 프로필을 생성하지 않은 불경한 자
		if(userProfile==null) return 3;
		
		//사진은 base64형식으로 고쳐서 보내기. 바이너리는 브라우저에서 못 읽는다.
		if(userProfile.getPicture()!=null) {
			userProfile.setPictureBase64(Base64.getEncoder().encodeToString(userProfile.getPicture()));
			userProfile.setPicture(null);
		}
		if(userProfile.getWatermark()!=null) {
			userProfile.setWatermarkBase64(Base64.getEncoder().encodeToString(userProfile.getWatermark()));
			userProfile.setWatermark(null);
		}
		
		//프로필이 있으면 세션에 넣기
		session.setAttribute("myProfile", userProfile);
		return 1;
	}
	
	//프로필 생성
	@Override
	public void insertMyProfile(HttpSession session, ProfileRequest p) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser==null) throw new Exception("로그인은 하고 와라 애송이");
		p.setUserNo(loginUser.getUserNo());
		
		//유효성 검사
		if(!ProfileValidator.profile(p)) throw new Exception("유효성 오류");
		
		//프로필 사진 있으면 Base64 디코딩, 리사이즈 및 소독
		byte[] picture = BinaryAndBase64.base64ToBinary(p.getPictureBase64());
		
		if (picture !=null) {
			//프로필 사진 있으면 picture에 넣기
			p.setPicture(picture);
			p.setPictureBase64(null);
		}
		
		//워터마크 사진 있으면 Base64 디코딩, 리사이즈 및 소독
		byte[] watermark = BinaryAndBase64.base64ToBinary(p.getWatermarkBase64());
		
		if (watermark!=null) {
			//워터마크 사진 있으면 watermark에 넣기
			p.setWatermark(watermark);
			p.setWatermarkBase64(null);
		}else {
			//워터마크가 없는데 개인 워터마크 쓸거에용 방지
			if(p.getWatermarkType().equals("C")) p.setWatermarkType("D");
		}
		
		int result = dao.insertMyProfile(sqlSession,p);
		
		if(!(result>0)) throw new Exception("새로운 프로파일 생성 실패!");
		
		//세션 업데이트
		selectMyProfile(session);
	}
	
	
	//프로필 업데이트
	@Override
	public void updateMyProfile(HttpSession session, ProfileRequest p) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser==null) throw new Exception("로그인은 하고 와라 애송이");
		p.setUserNo(loginUser.getUserNo());
		
		//유효성 검사
		if(!ProfileValidator.profile(p)) throw new Exception("유효성 오류");
		
		//프로필 사진 있으면 Base64 디코딩, 리사이즈 및 소독
		byte[] picture = BinaryAndBase64.base64ToBinary(p.getPictureBase64());
		
		if (picture !=null) {
			//프로필 사진 있으면 picture에 넣기
			p.setPicture(picture);
			p.setPictureBase64(null);
		}
		
		//워터마크 사진 있으면 Base64 디코딩, 리사이즈 및 소독
		byte[] watermark = BinaryAndBase64.base64ToBinary(p.getWatermarkBase64());
		
		if (watermark!=null) {
			//워터마크 사진 있으면 watermark에 넣기
			p.setWatermark(watermark);
			p.setWatermarkBase64(null);
		}else {
			//워터마크가 없는데 개인 워터마크 쓸거에용 방지
			if(p.getWatermarkType().equals("C")) p.setWatermarkType("D");
		}
		
		int result = dao.updateMyProfile(sqlSession,p);
		
		if(!(result>0)) throw new Exception("프로파일 갱신 실패!");
		
		//세션 업데이트
		selectMyProfile(session);
	}
	
	//챌린지 참가/성공/실패 시 프로필 업데이트
	@Override
	public void updateSessionMyProfile(HttpSession session) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		ProfileResponse userProfile = (ProfileResponse)session.getAttribute("myProfile");

		ProfileResponse updateProfile = dao.updateSessionMyProfile(sqlSession, loginUser.getUserNo());
		
		userProfile.setChalParticiapteCount(updateProfile.getChalParticiapteCount());
		userProfile.setSuccessCount(updateProfile.getSuccessCount());
		userProfile.setFailCount(updateProfile.getFailCount());
		userProfile.setSuccessRatio(updateProfile.getSuccessRatio());
		userProfile.setFailRatio(updateProfile.getFailRatio());
		
		session.setAttribute("myProfile", userProfile);
	}

	//비동기 - 프로필 세부조회
	@Override
	public ResponseEntity<ProfileResponse> profileDetail(int userNo) throws Exception {
		ProfileResponse profile = dao.profileDetail(sqlSession,userNo);
		if(profile == null) return ResponseEntity.status(404).build();
		
		if(profile.getPicture()!=null) {
			profile.setPictureBase64(Base64.getEncoder().encodeToString(profile.getPicture()));
			profile.setPicture(null);
		}
		
		return ResponseEntity.ok(profile);
	}
	
	//비동기 - 프로필 세부조회 챌린지 목록 불러오기
	@Override
	public ResponseEntity<List<ChallengeResponse>> chalParticipate(int userNo, int currentPage, String type) throws Exception {
		
		//프로필 열어봐도 되는 사람인지 확인
		String isOpen = dao.profileIsOpen(sqlSession, userNo);
		if(isOpen==null || !isOpen.equals("Y")) return ResponseEntity.status(404).build();
		
		SearchMyChallenge smc = SearchMyChallenge.builder()
				.currentPage(currentPage)
				.userNo(userNo)
				.searchType(type) //type : P(참여), O(생성)
				.build();
		
		List<ChallengeResponse> list = dao.chalParticipate(sqlSession, smc);
		
		if(list==null || list.isEmpty()) return ResponseEntity.status(404).build();
		
		//html태그 등 지우기
		for(ChallengeResponse chal : list) {
			chal.setContent(ChallengeFix.deleteContentTag(chal.getContent()));
		}
		
		return ResponseEntity.ok(list);
	}

}
