package com.kh.spring.ai.service;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentResponse;
import com.kh.spring.util.cryption.AESCryption;

@Service
public class GeminiEmbeddingServiceImpl implements GeminiEmbeddingService {

	private String apiKey;
	
	//잼미니 일해라
	@Override
	public ResponseEntity<Void> activateGemini(String keyStr) throws Exception {
		//열쇠를 받아 복호화해 되돌려주는 채신 택티껄 로-직(아님)
		apiKey = AESCryption.decryptionFile("src/main/resources/config/google-cloud-key.enc", keyStr);
		
		if(apiKey==null) return ResponseEntity.status(400).build();
		
		return ResponseEntity.ok().build();
	}

	//잼미니를 갈궈서 텍스트 임베딩
	@Override
	public float[] getEmbedding(String text) throws Exception {
		if(apiKey==null) throw new Exception("apiKey 활성화 해줘. 할 줄 모르면 담당자에게 문의하셈");
		
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
