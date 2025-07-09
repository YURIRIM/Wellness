package com.kh.spring.comments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.kh.spring.comments.model.service.CommentsService;

@Controller
public class CommentsController {
	//댓글 작성 기능 -> 대댓글, 게시물에 댓글 가능, 두 게시판에 모두 적용
	
	@Autowired
	private CommentsService service;
}
