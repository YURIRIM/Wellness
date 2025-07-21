package com.kh.spring.challenge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
			//연결하는데 쓰는 uuid반환
			return service.insertAtChal(session,file);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	//challenge content의 img 태그가 요청하는 경로
	@ResponseBody
	@GetMapping("/select")
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
			//연결하는데 쓰는 uuid반환
			return service.insertAtComment(session,file,chalNo);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	//비동기 - 댓글 사진 요청
	@ResponseBody
	@PostMapping("/selectComment")
	public ResponseEntity<byte[]> selectAtComment(@RequestBody List<Integer> commentNos) {
		try {
			return service.selectAtComment(commentNos);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
	
	
}
