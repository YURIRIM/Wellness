package com.kh.spring.challenge.model.service;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kh.spring.challenge.model.dao.ChalParticipationDao;
import com.kh.spring.challenge.model.vo.ChallengeResponse;
import com.kh.spring.challenge.model.vo.LoginUserAndChal;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.challenge.ChallengeValidator;

import jakarta.servlet.http.HttpSession;

@Service
public class ChalParticipationServiceImpl implements ChalParticipationService{
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private ChalParticipationDao dao;
	
	//비동기 - 챌린지 참여하기
	@Override
	public void insertParticipant(HttpSession session, int chalNo) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		
		//열였나요?
		String isOpen = dao.chalIsOpen(sqlSession, chalNo);
		if(isOpen == null || !isOpen.equals("Y")) throw new Exception("닫혔는데 어떻게 참여함???? 진짜모름");
		
		//너는 참여할 권한이 있니?
		LoginUserAndChal lac = LoginUserAndChal.builder()
				.userNo(loginUser.getUserNo())
				.chalNo(chalNo)
				.build();
		
		int result = dao.insertParticipant(sqlSession,lac);
		if(!(result>0)) throw new Exception("참여 할 수 없네용 까비아깝숑");
	}

	//비동기 - 챌린지 성공/실패하기
	@Override
	public ResponseEntity<List<ChallengeResponse>> updateParticipant(HttpSession session
			, int chalNo, String type) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		LoginUserAndChal lac = LoginUserAndChal.builder()
				.userNo(loginUser.getUserNo())
				.chalNo(chalNo)
				.status(type)
				.build();
		
		//유효성 검사
		if(!ChallengeValidator.chalParticipation(lac))
			return ResponseEntity.status(400).build();
		
		//참여자인가?
		String isParticipant = dao.loginUserIsParticipant(sqlSession, lac);
		if(isParticipant==null) return ResponseEntity.status(400).build();
		
		//챌린지 성공 혹은 실패 처리하기
		int result = dao.updateParticipant(sqlSession, lac);
		if(!(result>0)) return ResponseEntity.status(500).build();
		
		return ResponseEntity.ok().build();
	}
}
