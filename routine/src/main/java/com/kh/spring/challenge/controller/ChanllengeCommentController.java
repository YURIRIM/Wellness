package com.kh.spring.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.spring.challenge.model.service.ChallengeCommentService;
import com.kh.spring.challenge.model.vo.SearchComment;

@Controller
@RequestMapping("/chalComment")
public class ChanllengeCommentController {
	@Autowired
	private ChallengeCommentService service;
	
	//비동기 - 댓글 조회
	@ResponseBody
	@GetMapping("/selectComment")
	private ResponseEntity<byte[]> selectComment(Model model, SearchComment sc) {
		try {
			return service.selectComment(model,sc);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
	
	//비동기 - 대댓글 조회
	@ResponseBody
	@GetMapping("/selectRecomment")
	private ResponseEntity<byte[]> selectRecomment(Model model, SearchComment sc) {
		try {
			return service.selectRecomment(model,sc);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
	
	//성공시 'success' 실패시 'fail' 반환
	//joongBock : 직접 촬영한 사진 아닐 때 반환값
	//댓글 수정(contextPath+"/chalComment/deleteComment") post
	//삭제(contextPath+"/chalComment/updateComment") post
	//추가(contextPath+"/chalComment/insertComment") post
	//대댓글 작성(contextPath+"/chalComment/insertRecomment") post

	
}
