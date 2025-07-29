package com.kh.spring.challenge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.spring.challenge.model.service.ChalParticipationService;
import com.kh.spring.challenge.model.vo.ChallengeResponse;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/chalParticipation")
public class ChalParticipationController {
	@Autowired
	private ChalParticipationService service;
	
	//비동기 - 챌린지 참여하기
	@ResponseBody
	@PostMapping("/insert")
	public String insertParticipant(HttpSession session, int chalNo) {
		try {
			service.insertParticipant(session, chalNo);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	//비동기 - 챌린지 성공/실패하기
	@ResponseBody
	@GetMapping("/update")
	public ResponseEntity<List<ChallengeResponse>> updateParticipant(HttpSession session
			, int chalNo, String status) {
		try {
			return service.updateParticipant(session,chalNo,status);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
}
