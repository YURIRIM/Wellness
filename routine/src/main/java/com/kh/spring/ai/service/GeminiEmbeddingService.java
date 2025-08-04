package com.kh.spring.ai.service;

import org.springframework.http.ResponseEntity;

public interface GeminiEmbeddingService {

	float[] getEmbedding(String text) throws Exception;

	ResponseEntity<Void> activateGemini(String keyStr) throws Exception;

}