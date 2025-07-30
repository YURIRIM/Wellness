package com.kh.spring.controller;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.kh.spring.ai.service.GeminiEmbeddingService;
import com.kh.spring.videoCall.model.service.DailyService;

@Controller
public class MainController {
	@Autowired
	private GeminiEmbeddingService geminiService;
	@Autowired
	private DailyService dailyService;
	
	
	@GetMapping("/")
	public String main() {
		ImageIO.scanForPlugins();
		return "main";
	}
	
	 /**
     * 로그인 페이지
     */
    @GetMapping("/user/login")
    public String loginForm() {
        return "user/login";
    }
    
    //제미니 활성화
    @PostMapping("/activateAi")
    public ResponseEntity<Void> activateAi(String keyStr){
    	try {
    		int result = dailyService.activateDailCo(keyStr);
    		if(result==0) return ResponseEntity.status(400).build();
    		
    		return geminiService.activateGemini(keyStr); 
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(400).build();
		}
    }
}
