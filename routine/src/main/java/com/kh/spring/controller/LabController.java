package com.kh.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.spring.ai.service.GeminiEmbeddingService;
import com.kh.spring.challenge.model.service.ChallengeCategoryService;

@RequestMapping("/lab")
@Controller
public class LabController {
	@Autowired
	private GeminiEmbeddingService geminiService;
	@Autowired
	private ChallengeCategoryService categoryService;
    
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
	
	//잼미니 카테고리 추천 테스트
	@PostMapping("/categoryRecommend")
	public ResponseEntity<List<Integer>> cateogryRecommend(String inputText){
		try {
			return categoryService.recommend(null, inputText);
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}

}
