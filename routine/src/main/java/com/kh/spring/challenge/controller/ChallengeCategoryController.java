package com.kh.spring.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.spring.challenge.model.service.ChallengeCategoryService;

@RequestMapping("/category")
@Controller
public class ChallengeCategoryController {
	@Autowired
	private ChallengeCategoryService service;

	
	
	
}
