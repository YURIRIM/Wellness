package com.kh.spring.challenge.model.service;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.challenge.model.vo.Challenge;
import com.kh.spring.user.model.vo.User;

import jakarta.servlet.http.HttpSession;

@Service
public class ChallengeServiceImpl implements ChallengeService{
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	//챌린지 생성하기
	@Override
	public void newChall(HttpSession session, Model model
			, Challenge chall ,List<MultipartFile> files) throws Exception {
		User user = (User)session.getAttribute("loginUser");
		
		//challenge 유효성 확인
		if(ChallengeValidator.challenge(chall)) throw new Exception("challenge 유효성");
		
		
		
		

		
		
	}
}
