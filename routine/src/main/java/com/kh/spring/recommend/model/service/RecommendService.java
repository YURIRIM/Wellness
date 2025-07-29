package com.kh.spring.recommend.model.service;

import com.kh.spring.recommend.model.vo.Location;
import com.kh.spring.recommend.model.vo.Weather;
import com.kh.spring.recommend.model.vo.Recommend;
import java.util.List;

public interface RecommendService {
    
    Location getLocationByCoords(double latitude, double longitude);
    
    Weather getWeatherInfo(Location location);
    
    List<Recommend> getRecommendations(Weather weather);

    List<Location> getAllWeatherLocations(); 

    Location getWeatherAndRecommendationsByCoords(double latitude, double longitude);
}