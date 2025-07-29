package com.kh.spring.recommend.model.service;

import com.kh.spring.recommend.model.vo.Location;
import com.kh.spring.recommend.model.vo.Weather;
import com.kh.spring.recommend.model.vo.Recommend;
import java.util.List;

public interface RecommendService {
    
    // GPS 좌표로 위치 정보 처리
    Location getLocationByCoords(double latitude, double longitude);
    
    // 날씨 정보 가져오기
    Weather getWeatherInfo(Location location);
    
    // 운동 추천
    List<Recommend> getRecommendations(Weather weather);
}