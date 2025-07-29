package com.kh.spring.challenge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.spring.challenge.model.service.ProfileService;
import com.kh.spring.challenge.model.vo.ChallengeResponse;
import com.kh.spring.challenge.model.vo.ProfileRequest;
import com.kh.spring.challenge.model.vo.ProfileResponse;

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
	
	//프로필 세부조회 페이지로
	@GetMapping("/detail")
	public String goProfileDetail(Model model, int userNo) {
		model.addAttribute("userNo",userNo);
		return "profile/detail";
	}
	
	//비동기 - 프로필 세부조회
	@ResponseBody
	@GetMapping("/searchDetail")
	public ResponseEntity<ProfileResponse> profileDetail(int userNo) {
		try {
			return service.profileDetail(userNo);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
	
	//비동기 - 프로필 세부조회 참여 챌린지 목록
	@ResponseBody
	@GetMapping("/chalParticipate")
	public ResponseEntity<List<ChallengeResponse>> chalParticipate(int userNo, int currentPage, String type) {
		try {
			return service.chalParticipate(userNo, currentPage, type);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
}
