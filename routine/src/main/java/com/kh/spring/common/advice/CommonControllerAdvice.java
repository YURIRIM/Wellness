package com.kh.spring.common.advice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.kh.spring.challenge.model.service.ChallengeService;
import com.kh.spring.challenge.model.vo.ChallengeCategory;

import jakarta.servlet.http.HttpSession;


@ControllerAdvice
public class CommonControllerAdvice {
	@Autowired
	private ChallengeService chalService;
	
//	@ModelAttribute("loginUser")
//	public Member addLoginUser(HttpSession session) {
//		if(session.getAttribute("loginUser") != null) {
//			return (Member) session.getAttribute("loginUser");
//		} else {
//			return null;
//		}
//	}
	
	//ChallengeCategory 넣어주기
	@ModelAttribute("ChallengeCategory")
	public ChallengeCategory addLoginUser(HttpSession session) {
		
		if(session.getAttribute("ChallengeCategory") == null) {
			//CC가 없으면 조회해서 세션에 넣어주기
//			List<ChallengeCategory> ccList = chalService.selectCCList();
//			session.setAttribute("ChallengeCategory", ccList);
		}
		
		return (ChallengeCategory) session.getAttribute("ChallengeCategory");
	}
}
