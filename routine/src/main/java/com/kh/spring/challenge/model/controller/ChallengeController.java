package com.kh.spring.challenge.model.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/challenge")
public class ChallengeController {
	
	@GetMapping("/callMain")
	public String goCallMain() {
		return "challenge/callMain";
	}
}
