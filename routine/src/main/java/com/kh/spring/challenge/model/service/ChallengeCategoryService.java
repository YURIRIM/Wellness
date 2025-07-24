package com.kh.spring.challenge.model.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.kh.spring.challenge.model.vo.ChallengeCategory;

public interface ChallengeCategoryService {

	List<ChallengeCategory> selectCCList() throws Exception;

	ResponseEntity<Void> categoryToVector() throws Exception;

	ResponseEntity<List<Integer>> recommend(String inputText, String content) throws Exception;

}
