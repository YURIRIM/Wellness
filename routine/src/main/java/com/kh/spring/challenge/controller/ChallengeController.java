package com.kh.spring.challenge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.challenge.model.service.ChallengeService;
import com.kh.spring.challenge.model.vo.Challenge;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/challenge")
public class ChallengeController {
	@Autowired
	private ChallengeService service;
	
	//챌린지 메인 화면으로
	@GetMapping("/chalMain")
	public String goChalMain() {
		return "challenge/chalMain";
	}

	//새로운 챌린지 생성하기 - newChall조각 반환
	@GetMapping("/newChal")
	public String getNewChal() {
		return "fragments :: newChal";
	}
	
	//내가 생성한 챌린지로
	@GetMapping("/createdChal")
	public String goCreatedChal() {
		return "challenge/createdChal";
	}
	
	//내가 참여한 챌린지로
	@GetMapping("/joinedChal")
	public String goJoinedChal() {
		return "challenge/joinedChal";
	}
	
	//새로운 챌린지 생성하기
	@PostMapping("/newChal")
	public String newChall(HttpSession session ,Model model
			,Challenge chal ,List<MultipartFile> files) {
		try {
			service.newChal(session, model, chal, files);
		} catch (Exception e) {
			e.printStackTrace();
			return "common/errorPage";
		}
		return "challenge/chalDetail";
	}
	
}
