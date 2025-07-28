package com.kh.spring.videoCall.model.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.kh.spring.videoCall.model.vo.Challenge;
import com.kh.spring.videoCall.model.vo.VideoCallRequest;
import com.kh.spring.videoCall.model.vo.VideoCallResponse;

import jakarta.servlet.http.HttpSession;

public interface VideoCallService {

	ResponseEntity<String> createRoom(HttpSession session, VideoCallRequest vcr) throws Exception;

	ResponseEntity<List<VideoCallResponse>> createdRoom(HttpSession session) throws Exception;

	ResponseEntity<List<Challenge>> notCreatedRoom(HttpSession session) throws Exception;

	void challengeSanitizer(List<Challenge> challenge) throws Exception;

	void vcResponseSanitizer(List<VideoCallResponse> vcr) throws Exception;

	ResponseEntity<List<VideoCallResponse>> invitedRoom(HttpSession session) throws Exception;

	ResponseEntity<Void> openRoom(HttpSession session, String roomUuidStr) throws Exception;
}
