package com.kh.spring.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.challenge.model.service.AttachmentService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/attachment")
public class AttachmentController {
	@Autowired
	private AttachmentService service;
	
	//비동기 - chal summernote에서 사진 넣기
	@ResponseBody
	@PostMapping("/insert")
	public String insertAtChal(HttpSession session, MultipartFile file) {
		try {
			return service.insertAtChal(session,file);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	//비동기 - chalDetail에서 사진 조회
	@ResponseBody
	@PostMapping("/select")
	public ResponseEntity<byte[]> selectAtChal(HttpSession session, String at) {
		try {
			return service.selectAtChal(session,at);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
	
	//비동기 - chalDetail에서 댓글 사진 업로드
	@ResponseBody
	@PostMapping("/insertComment")
	public String insertAtComment(HttpSession session, MultipartFile file, int chalNo) {
		try {
			return service.insertAtComment(session,file,chalNo);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	
}
