package com.kh.spring.recommend.controller;

import com.kh.spring.recommend.model.service.RecommendService;
import com.kh.spring.recommend.model.vo.Location;
import com.kh.spring.recommend.model.vo.Weather;
import com.kh.spring.recommend.model.vo.Recommend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import org.springframework.http.ResponseEntity; 

@Controller
@RequestMapping("/recommend") 
public class RecommendController {
    
    @Autowired
    private RecommendService recommendService;

    @Value("${api.kakao.js.key}")
    private String kakaoJsApiKey;
    
    @GetMapping("") 
    public String recommend() {
        return "recommend/recommend";  
    }
    
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
            e.printStackTrace(); 
        }
        
        return "recommend/result";
    }
    
    @GetMapping("/map")
    public String showWeatherMap(Model model) {
        model.addAttribute("kakaoJsApiKey", kakaoJsApiKey); 
        return "recommend/weatherMap"; 
    }

    @GetMapping("/map-data")
    @ResponseBody 
    public List<Location> getWeatherMapData() {
        return recommendService.getAllWeatherLocations();
    }

    @GetMapping("/weather-by-coords")
    @ResponseBody 
    public ResponseEntity<Location> getWeatherAndRecommendationsByCoords(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        try {
            Location data = recommendService.getWeatherAndRecommendationsByCoords(latitude, longitude);
            
            if (data != null && data.getWeatherObject() != null) {
                return ResponseEntity.ok(data); 
            } else {
                return ResponseEntity.notFound().build(); 
            }
        } catch (Exception e) {
            System.err.println("특정 좌표 날씨 정보 가져오기 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build(); 
        }
    }
}