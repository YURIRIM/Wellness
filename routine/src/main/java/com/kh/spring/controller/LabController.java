package com.kh.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.spring.ai.service.GeminiEmbeddingService;

@RequestMapping("/lab")
@Controller
public class LabController {
	@Autowired
	private GeminiEmbeddingService geminiService;
    
	//랩실로
    @GetMapping("")
    public String goLab() {
    	return "common/lab";
    }
	
    //잼미니 임베딩 테스트
	@PostMapping("/geminiEmbeddingTest")
	public ResponseEntity<Void> geminiTest(String inputText) {
		try {
			geminiService.getEmbedding(inputText);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}

}
