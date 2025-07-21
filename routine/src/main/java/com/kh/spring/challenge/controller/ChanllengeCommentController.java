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

import com.kh.spring.challenge.model.service.ChallengeCommentService;
import com.kh.spring.challenge.model.vo.ChallengeCommentRequest;
import com.kh.spring.challenge.model.vo.ChallengeCommentResponse;
import com.kh.spring.challenge.model.vo.SearchComment;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/chalComment")
public class ChanllengeCommentController {
	@Autowired
	private ChallengeCommentService service;
	
	//비동기 - 댓글 조회
	@ResponseBody
	@GetMapping("/selectComment")
	private ResponseEntity<List<ChallengeCommentResponse>> selectComment(Model model, SearchComment sc) {
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
	private ResponseEntity<List<ChallengeCommentResponse>> selectRecomment(Model model, SearchComment sc) {
		try {
			return service.selectRecomment(model,sc);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
	
	//비동기 - 댓글 추가
	@ResponseBody
	@PostMapping("insertComment")
	private String insertComment(HttpSession session, ChallengeCommentRequest ccr) {
		try {
			service.insertComment(session,ccr);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	//비동기 - 댓글 수정
	@ResponseBody
	@PostMapping("updateComment")
	private String updateComment(HttpSession session, ChallengeCommentRequest ccr) {
		try {
			service.updateComment(session,ccr);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	//비동기 - 댓글 삭제
	@ResponseBody
	@PostMapping("deleteComment")
	private String deleteComment(HttpSession session, int commentNo) {
		try {
			service.deleteComment(session,commentNo);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
}
