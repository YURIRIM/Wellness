package com.kh.spring.challenge.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.spring.challenge.model.service.ProfileService;
import com.kh.spring.challenge.model.vo.Profile;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/profile")
public class ProfileController {
	@Autowired
	private ProfileService service;
	
	
	//내 프로필 확인하기
	@PostMapping("/selectMyProfile")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> selectMyProfile(HttpSession session) {
		Map<String, Object> response = new HashMap<>();
	    try {
	        String result = service.selectMyProfile(session);
	        switch (result) {
	            case "0": //오류
	                response.put("status", "false");
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	            case "2": //로그인 안됨
	                response.put("status", "noUser");
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	            case "3": //프로필 없음
	                response.put("status", "noProfile");
	                return ResponseEntity.ok(response);
	            default: //프로필 jwt로 반환
	                response.put("status", "success");
	                response.put("data", result);
	                return ResponseEntity.ok(response);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("status", "false");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	//프로필 생성 주소로 이동
	@GetMapping("/insertMyProfile")
	public String goInsetMyProfile() {
		return "profile/insertMyProfile";
	}
	
	//프로필 생성
	@PostMapping("/insertMyProfile")
	public String insertMyProfile(HttpSession session, Profile p) {
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
	
	//프로필 생성
	@PostMapping("/updateMyProfile")
	public String updateMyProfile(HttpSession session, Profile p) {
		try {
			service.updateMyProfile(session,p);
			return "redirect:/challenge/chalMain";
		} catch (Exception e) {
			e.printStackTrace();
			return "common/errorPage";
		}
	}

	
}
