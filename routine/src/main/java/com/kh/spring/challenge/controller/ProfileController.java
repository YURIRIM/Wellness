package com.kh.spring.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.spring.challenge.model.service.ProfileService;
import com.kh.spring.challenge.model.vo.ProfileRequest;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/profile")
public class ProfileController {
	@Autowired
	private ProfileService service;
	
	//내 프로필 확인하기
	@PostMapping("/selectMyProfile")
	@ResponseBody
	public void selectMyProfile(HttpSession session) {
	    try {
	        int result = service.selectMyProfile(session);
	        if(result!=1) throw new Exception("프로필 조회 실패");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	//프로필 생성 주소로 이동
	@GetMapping("/insertMyProfile")
	public String goInsetMyProfile() {
		return "profile/insertMyProfile";
	}
	
	//프로필 생성
	@PostMapping("/insertMyProfile")
	public String insertMyProfile(HttpSession session, ProfileRequest p) {
		try {
			service.insertMyProfile(session,p);
			return "redirect:/challenge/chalMain";
		} catch (Exception e) {
			e.printStackTrace();
			return "common/errorPage";
		}
	}
	
	//프로필 갱신 주소로 이동
	@GetMapping("/updateMyProfile")
	public String goUpdateMyProfile() {
		return "profile/updateMyProfile";
	}
	
	//프로필 갱신
	@PostMapping("/updateMyProfile")
	public String updateMyProfile(HttpSession session, ProfileRequest p) {
		try {
			service.updateMyProfile(session,p);
			return "redirect:/challenge/chalMain";
		} catch (Exception e) {
			e.printStackTrace();
			return "common/errorPage";
		}
	}
}
