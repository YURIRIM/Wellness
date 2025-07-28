package com.kh.spring.videoCall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.spring.videoCall.model.service.VideoCallService;
import com.kh.spring.videoCall.model.vo.Challenge;
import com.kh.spring.videoCall.model.vo.VideoCallRequest;
import com.kh.spring.videoCall.model.vo.VideoCallResponse;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/videoCall")
public class VideoCallController {
	@Autowired
	private VideoCallService service;

	//화상회의 메인 화면으로
	@GetMapping("/main")
	public String goVcMain() {
		return "main";
	}
	
	//비동기 - 화상회의 생성하기
	@PostMapping("/createRoom")
	public ResponseEntity<String> createRoom(HttpSession session, VideoCallRequest vcr){
		try {
			return service.createRoom(session,vcr);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
	
	//비동기 - 생성하지 않은 방 조회
	@PostMapping("/notCreatedRoom")
	public ResponseEntity<List<Challenge>> notCreatedRoom(HttpSession session){
		try {
			return service.notCreatedRoom(session);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
	
	//비동기 - 생성한 방 조회
	@PostMapping("/createdRoom")
	public ResponseEntity<List<VideoCallResponse>> createdRoom(HttpSession session){
		try {
			return service.createdRoom(session);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
	
	//비동기 - 참여한 방 조회
	@PostMapping("/invitedRoom")
	public ResponseEntity<List<VideoCallResponse>> invitedRoom(HttpSession session){
		try {
			return service.invitedRoom(session);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
}
