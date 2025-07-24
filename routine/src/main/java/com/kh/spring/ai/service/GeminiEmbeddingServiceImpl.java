package com.kh.spring.ai.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentResponse;

@Service
public class GeminiEmbeddingServiceImpl implements GeminiEmbeddingService {

	@Value("${google.genai.api-key}")
	private String apiKey;

	//잼미니를 갈궈서 텍스트 임베딩
	@Override
	public float[] getEmbedding(String text) throws Exception {
		//이게 무슨 뜻인지 아니?
	    Client client = Client.builder().apiKey(apiKey).build();
	    
	    //난 몰라!
	    EmbedContentResponse response = client.models.embedContent(
	        "gemini-embedding-001", text, null
	    		);

	    List<ContentEmbedding> embs = response.embeddings()
	                                          .orElse(Collections.emptyList());
	    
	    if (embs.isEmpty()) throw new RuntimeException("임베딩 결과가 없습니다.");

	    ContentEmbedding firstEmb = embs.get(0);
	    List<Float> vectorList = firstEmb.values().orElse(Collections.emptyList());

	    if (vectorList.isEmpty()) throw new RuntimeException("벡터 데이터가 없습니다.");

	    float[] vectorArray = new float[vectorList.size()];
	    for (int i = 0; i < vectorList.size(); i++) {
	        vectorArray[i] = vectorList.get(i);
	    }
	    
	    return vectorArray;
	}
}
