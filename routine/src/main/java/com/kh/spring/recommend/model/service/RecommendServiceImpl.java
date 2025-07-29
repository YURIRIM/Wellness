package com.kh.spring.recommend.model.service;

import com.kh.spring.recommend.model.dao.RecommendDao;
import com.kh.spring.recommend.model.vo.Location;
import com.kh.spring.recommend.model.vo.Weather;
import com.kh.spring.recommend.model.vo.Recommend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendServiceImpl implements RecommendService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private RecommendDao recommendDao;
    
    @Value("${api.weather.key}")
    private String weatherApiKey;
    
    @Value("${api.air.key}")
    private String airApiKey;
    
    @Value("${api.kakao.key}")
    private String kakaoApiKey;
    
    @Override
    public Location getLocationByCoords(double latitude, double longitude) {
        Location location = new Location();
        
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        
        location.setLocationName("현재 위치");
        location.setAddress("GPS 기반 위치");
        
        location.setNx(60);
        location.setNy(127);
        
        recommendDao.insertLocation(location);
        
        return location;
    }
    
    @Override
    public Weather getWeatherInfo(Location location) {
        Weather weather = new Weather();
        weather.setLocationNo(location.getLocationNo());
        
        weather.setTemperature(25);
        weather.setHumidity(60);
        weather.setRainProbability(30);
        weather.setWeatherCondition("맑음");
        weather.setPm10(45);
        weather.setAirGrade("보통");
        
        recommendDao.insertWeather(weather);
        
        return weather;
    }
    
    @Override
    public List<Recommend> getRecommendations(Weather weather) {
        List<Recommend> recommendations = new ArrayList<>();
        
        if (weather.getTemperature() >= 5 && weather.getTemperature() <= 30) {
            if (weather.getPm10() <= 80) {  
                if (weather.getRainProbability() <= 50) {  
                    addRecommendation(recommendations, weather.getWeatherNo(), "조깅", "OUTDOOR");
                    addRecommendation(recommendations, weather.getWeatherNo(), "축구", "OUTDOOR");
                    addRecommendation(recommendations, weather.getWeatherNo(), "농구", "OUTDOOR");
                    addRecommendation(recommendations, weather.getWeatherNo(), "자전거", "OUTDOOR");
                }
            }
        }
        
        addRecommendation(recommendations, weather.getWeatherNo(), "헬스장", "INDOOR");
        addRecommendation(recommendations, weather.getWeatherNo(), "복싱", "INDOOR");
        
        if (weather.getTemperature() > 28) {
            addRecommendation(recommendations, weather.getWeatherNo(), "수영", "INDOOR");
            addRecommendation(recommendations, weather.getWeatherNo(), "실내 헬스장", "INDOOR");
        } else {
            addRecommendation(recommendations, weather.getWeatherNo(), "수영", "INDOOR");
        }
        
        if (weather.getTemperature() < 5) {
            addRecommendation(recommendations, weather.getWeatherNo(), "홈트레이닝", "INDOOR");
            addRecommendation(recommendations, weather.getWeatherNo(), "탁구", "INDOOR");
        }
        
        return recommendations;
    }
    
    private void addRecommendation(List<Recommend> list, int weatherNo, String exerciseType, String locationType) {
        Recommend recommend = new Recommend();
        recommend.setWeatherNo(weatherNo);
        recommend.setExerciseType(exerciseType);
        recommend.setLocationType(locationType);
        
        recommendDao.insertRecommend(recommend);
        list.add(recommend);
    }
}