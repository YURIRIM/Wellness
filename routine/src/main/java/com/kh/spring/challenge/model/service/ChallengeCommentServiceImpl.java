package com.kh.spring.challenge.model.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.kh.spring.challenge.model.dao.AttachmentDao;
import com.kh.spring.challenge.model.dao.ChallengeCommentDao;
import com.kh.spring.challenge.model.dao.ChallengeDao;
import com.kh.spring.challenge.model.vo.ChallengeCommentRequest;
import com.kh.spring.challenge.model.vo.ChallengeCommentResponse;
import com.kh.spring.challenge.model.vo.SearchComment;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.challenge.ChallengeCommentValidator;
import com.kh.spring.util.common.UuidUtil;

import jakarta.servlet.http.HttpSession;

@Service
public class ChallengeCommentServiceImpl implements ChallengeCommentService{
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private ChallengeCommentDao dao;
	@Autowired
	private ChallengeDao chalDao;
	@Autowired
	private AttachmentDao atDao;
	@Autowired
	private AttachmentService atService;

	//댓글 조회
	@Override
	public ResponseEntity<byte[]> selectComment(Model model, SearchComment sc) throws Exception {
		List<ChallengeCommentResponse> ccs = dao.chalDetailComment(sqlSession,sc);
		
		//대댓글 조회
		for(ChallengeCommentResponse cc : ccs) {
			sc.setCurrentPage(0);//페이징 초기화
			sc.setRecommentTarget(cc.getCommentNo());
			ccs.addAll(dao.chalDetailRecomment(sqlSession,sc));
		}
		
		for(ChallengeCommentResponse cc : ccs) {
			//댓글 작성자 isOpen!="A" 인 사람 빼고 프로필사진 base64하기
			if(!cc.getIsOpen().equals("A")) {
				byte[] picture = cc.getPicture();
				if(picture!=null) {
					cc.setPictureBase64(Base64.getEncoder().encodeToString(picture));
				}
			}else cc.setNick(null); //A이면 nick도 말소
			cc.setPicture(null);
			
			//댓글 상태가 D면 D져.
			if(cc.getStatus().equals("D")) {
				ChallengeCommentResponse newCc = ChallengeCommentResponse.builder()
						.chalNo(cc.getChalNo())
						.commentNo(cc.getCommentNo())
						.status("D")
						.build();
				cc = newCc;
			}
		}
		
		model.addAttribute("chalDetailComment", ccs);
		model.addAttribute("currentPage", sc.getCurrentPage());
		
		//댓글 사진 조회해서 반납
		return atService.selectAtComment(ccs);
	}
	
	
	//대댓글 조회
	@Override
	public ResponseEntity<byte[]> selectRecomment(Model model, SearchComment sc) throws Exception {
		List<ChallengeCommentResponse> ccs = dao.chalDetailRecomment(sqlSession,sc);

		for(ChallengeCommentResponse cc : ccs) {
			//댓글 작성자 isOpen!="A" 인 사람 빼고 프로필사진 base64하기
			if(!cc.getIsOpen().equals("A")) {
				byte[] picture = cc.getPicture();
				if(picture!=null) {
					cc.setPictureBase64(Base64.getEncoder().encodeToString(picture));
				}
			}else cc.setNick(null); //A이면 nick도 말소
			cc.setPicture(null);
			
			//상태가 D졌어? 그럼 죽어.
			if(cc.getStatus().equals("D")) {
				ChallengeCommentResponse newCc = ChallengeCommentResponse.builder()
						.chalNo(cc.getChalNo())
						.commentNo(cc.getCommentNo())
						.recommentTarget(cc.getRecommentTarget())
						.status("D")
						.build();
				cc = newCc;
			}
		}
		
		model.addAttribute("chalDetailComment", ccs);
		return atService.selectAtComment(ccs);
	}

	//비동기 - 댓글 생성
	@Override
	@Transactional
	public void insertComment(HttpSession session, ChallengeCommentRequest ccr) throws Exception{
		User loginUser = (User)session.getAttribute("loginUser");
		int result=1;
		
		//해당 회원이 댓글 쌀 능력이 있는지 확인
		Map<String, Integer> map = new HashMap<>() {
			//직렬..화? 데...팩토 같은 건가?
			private static final long serialVersionUID = 1L;
			{
		    put("userNo", loginUser.getUserNo());
		    put("chalNo", ccr.getChalNo());
		}};
		String isParticipant = chalDao.loginUserIsParticipant(sqlSession,map);
		if(isParticipant==null || !isParticipant.equals("Y"))
			throw new Exception("사기꾼이다!!");
		
		//유효성 검사
		if(ChallengeCommentValidator.challengeComment(ccr))
			throw new Exception("댓글 유효성 오류!");
		
		//attachment 사진 uuid로 활성화
		if(ccr.getUuidStr()!=null) {
			result *= atDao.connectAtbyUuid(sqlSession
					,UuidUtil.strToByteArr(ccr.getUuidStr()));
			ccr.setUuidStr(null);
		}
		
		//DB에 저장
		ccr.setUserNo(loginUser.getUserNo());
		result *= dao.insertComment(sqlSession, ccr);
		
		if(result==0) throw new Exception("DB insert 오류");
	}
	
	//댓글 업데이트
	@Override
	@Transactional
	public void updateComment(HttpSession session, ChallengeCommentRequest ccr) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		int result=1;
		
		//해당 회원이 댓글 쌀 능력이 있는지 확인
		Map<String, Integer> map = new HashMap<>() {
			//직렬..화? 데...팩토 같은 건가?
			private static final long serialVersionUID = 1L;
			{
		    put("userNo", loginUser.getUserNo());
		    put("chalNo", ccr.getChalNo());
		}};
		String isParticipant = chalDao.loginUserIsParticipant(sqlSession,map);
		if(isParticipant==null || !isParticipant.equals("Y"))
			throw new Exception("사기꾼이다!!");
		
		//유효성 검사
		if(ChallengeCommentValidator.challengeComment(ccr))
			throw new Exception("댓글 유효성 오류!");
			
		//attachment 사진 uuid로 활성화
		if(ccr.getUuidStr()!=null && !ccr.getUuidStr().equals("1")) {
			//사진이 없거나 변경되지 않으면(1) 곱게 가라
			
			//사진 변경됨 -> 기존 사진 비활성화, 새로운 사진 활성화
			result *= atDao.disconnectCommentToAt(sqlSession, ccr.getCommentNo());
			
			//사진 활성화
			byte[] uuid = UuidUtil.strToByteArr(ccr.getUuidStr());
			result *= atDao.connectAtbyUuid(sqlSession, uuid);
			ccr.setUuidStr(null);
		}
		
		//DB에 저장
		ccr.setUserNo(loginUser.getUserNo());
		result *= dao.updateComment(sqlSession, ccr);
		
		if(result==0) throw new Exception("DB insert 오류");
	}
	
	//댓글 삭제
	@Override
	@Transactional
	public void deleteComment(HttpSession session, ChallengeCommentRequest ccr) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		ccr.setUserNo(loginUser.getUserNo());
		
		//댓글 비활성화
		int result = dao.deleteComment(sqlSession,ccr);
		if(!(result>0)) throw new Exception("댓글 비활성화 실패");
		
		//사진 비활성화
		atDao.disconnectCommentToAt(sqlSession, ccr.getCommentNo());

	}
}
