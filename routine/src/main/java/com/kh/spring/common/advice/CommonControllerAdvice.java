package com.kh.spring.common.advice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.kh.spring.challenge.model.service.ChallengeCategoryService;
import com.kh.spring.challenge.model.service.ProfileService;
import com.kh.spring.challenge.model.vo.ChallengeCategory;
import com.kh.spring.challenge.model.vo.ProfileResponse;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.common.Dummy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@ControllerAdvice
public class CommonControllerAdvice {
	@Autowired
	private ChallengeCategoryService categoryService;
	@Autowired
	private ProfileService profileService;
	
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
				ccList = categoryService.selectCCList();
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
	
	
	//myProfile 넣기
	@ModelAttribute("myProfile")
	public ProfileResponse goMyProfile(HttpSession session, HttpServletRequest request) {
		try {
			if(session.getAttribute("myProfile") == null) {
				int result = profileService.selectMyProfile(session);
				if (result!=1) return null;
			}
			return (ProfileResponse) session.getAttribute("myProfile");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
