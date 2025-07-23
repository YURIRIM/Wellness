package com.kh.spring.ai.service;

public interface GeminiEmbeddingService {

	float[] getEmbedding(String text) throws Exception;

}
