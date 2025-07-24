package com.kh.spring.challenge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.spring.challenge.model.service.ChallengeCategoryService;

@RequestMapping("/category")
@Controller
public class ChallengeCategoryController {
	@Autowired
	private ChallengeCategoryService service;
	
	//비동기 - 카테고리 임베딩 해놓기
	@PostMapping("/embedding")
	public ResponseEntity<Void> categoryToVector(){
		try {
			return service.categoryToVector();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
	
	//비동기 - 카테고리 추천받기
	@PostMapping("/recommend")
	public ResponseEntity<List<Integer>> recommend(String title, String content){
		try {
			return service.recommend(title, content);
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}
	
	
}
