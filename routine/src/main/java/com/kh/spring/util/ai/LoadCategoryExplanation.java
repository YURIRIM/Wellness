package com.kh.spring.util.ai;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.kh.spring.challenge.model.vo.ChallengeCategory;

public class LoadCategoryExplanation {
	
	//config/category-explanation.properties내 파일 불러오기
	public static List<ChallengeCategory> getExplanation() throws Exception {
	    Properties props = new Properties();
	    try (InputStream is = LoadCategoryExplanation.class.getClassLoader().getResourceAsStream("config/category-explanation.properties");
	    	     InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
	    	    props.load(reader);}
	    
	    List<ChallengeCategory> explanationList = new ArrayList<>();
	    for (String key : props.stringPropertyNames()) {
	        if (key.startsWith("no.")) {
	            int keyNum = Integer.parseInt(key.substring(3));  // "no." 이후 숫자를 int로 변환
	            explanationList.add(ChallengeCategory.builder()
	            		.categoryNo(keyNum)
	            		.embedding(props.getProperty(key))
	            		.build());
	        }
	    }
	    return explanationList;
	}
}
