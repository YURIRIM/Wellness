package com.kh.spring.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.kh.spring.challenge.model.service.ChallengeCommentService;

@Controller
public class ChanllengeCommentController {
	@Autowired
	private ChallengeCommentService service;
	
	
	
}
