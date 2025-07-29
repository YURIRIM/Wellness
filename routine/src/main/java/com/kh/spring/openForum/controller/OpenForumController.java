package com.kh.spring.openForum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.kh.spring.openForum.model.service.OpenForumService;

@Controller
public class OpenForumController {

	@Autowired
	private OpenForumService service;
	
	//자유게시판 기능 메소드 작성 예정
}
