package com.kh.spring.challenge.model.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

public interface AttachmentService {

	String insertAtChal(HttpSession session, MultipartFile at) throws Exception;

	ResponseEntity<byte[]> selectAtChal(HttpSession session, String at) throws Exception;
	
	ResponseEntity<String> insertAtComment(HttpSession session, MultipartFile file, int chalNo) throws Exception;

	ResponseEntity<byte[]> selectAtComment(List<Integer> commentNos) throws Exception;

	ResponseEntity<byte[]> defaultImg(String filename) throws Exception;
	
}
