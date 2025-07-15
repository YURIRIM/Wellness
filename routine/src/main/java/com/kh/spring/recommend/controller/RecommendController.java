package com.kh.spring.recommend.controller;

import com.kh.spring.recommend.model.service.RecommendService;
import com.kh.spring.recommend.model.vo.Location;
import com.kh.spring.recommend.model.vo.Weather;
import com.kh.spring.recommend.model.vo.Recommend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/routine") 
public class RecommendController {
    
    @Autowired
    private RecommendService recommendService;
    
    // 메인 페이지
    @GetMapping("/recommend") 
    public String recommend() {
        return "recommend/recommend";  
    }
    
    // GPS 기반 운동 추천 처리
    @PostMapping("/current-location")  
    public String getCurrentLocationRecommend(
        @RequestParam double latitude,
        @RequestParam double longitude,
        Model model) {
        
        try {
            Location location = recommendService.getLocationByCoords(latitude, longitude);
            Weather weather = recommendService.getWeatherInfo(location);
            List<Recommend> recommendations = recommendService.getRecommendations(weather);
            
            model.addAttribute("location", location);
            model.addAttribute("weather", weather);
            model.addAttribute("recommendations", recommendations);
            
        } catch (Exception e) {
            model.addAttribute("error", "현재 위치의 추천 정보를 가져올 수 없습니다: " + e.getMessage());
        }
        
        return "recommend/result";
    }
    
    // 테스트용
    @GetMapping("/test")  
    public String testRecommend(Model model) {
        try {
            Location location = recommendService.getLocationByCoords(37.5665, 126.9780);
            Weather weather = recommendService.getWeatherInfo(location);
            List<Recommend> recommendations = recommendService.getRecommendations(weather);
            
            model.addAttribute("location", location);
            model.addAttribute("weather", weather);
            model.addAttribute("recommendations", recommendations);
            
        } catch (Exception e) {
            model.addAttribute("error", "테스트 추천 실패: " + e.getMessage());
        }
        
        return "recommend/result";
    }
}