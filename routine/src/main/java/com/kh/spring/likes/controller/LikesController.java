package com.kh.spring.likes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.kh.spring.likes.model.service.LikesService;

@Controller
public class LikesController {
	
	@Autowired
	private LikesService service;
	
}
