package com.kh.spring.common.advice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.kh.spring.challenge.model.service.ChallengeService;
import com.kh.spring.challenge.model.vo.ChallengeCategory;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.common.Dummy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@ControllerAdvice
public class CommonControllerAdvice {
	@Autowired
	private ChallengeService chalService;
	
	@ModelAttribute("loginUser")
	public User addLoginUser(HttpSession session) {
		if(session.getAttribute("loginUser") != null) {
			return (User) session.getAttribute("loginUser");
		} else {
			return null;
		}
	}
	
	//ChallengeCategory 넣어주기
	@ModelAttribute("ChallengeCategory")
	public List<ChallengeCategory> addChallengeCategory(HttpSession session) {
		List<ChallengeCategory> ccList = new ArrayList<>();
		try {
			if(session.getAttribute("ChallengeCategory") == null
					||((List<ChallengeCategory>)session.getAttribute("ChallengeCategory")).isEmpty()) {
				//CC가 없으면 조회해서 세션에 넣어주기
				ccList = chalService.selectCCList();
				session.setAttribute("ChallengeCategory", ccList);
			}
		} catch (Exception e) {
			//DB접속이 불가능한 경우 - 더미 데이터 넣기
			System.out.println("DB접속 불가능 - dummyCC 발동");
			ccList = Dummy.dummyCC();
			session.setAttribute("ChallengeCategory", ccList);
		}
		return (List<ChallengeCategory>) session.getAttribute("ChallengeCategory");
	}
	
	//root-context 넣기
	@ModelAttribute("rootContextPath")
	public String getRoot(HttpSession session, HttpServletRequest request) {
		try {
	        String contextPath = request.getContextPath();
	        session.setAttribute("rootContextPath", contextPath);
	        return contextPath;
		} catch (Exception e) {
			return "/routine";
		}
	}
	
	
}
