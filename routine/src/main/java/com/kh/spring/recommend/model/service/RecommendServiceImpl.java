package com.kh.spring.recommend.model.service;

import com.kh.spring.recommend.model.dao.RecommendDao;
import com.kh.spring.recommend.model.vo.Location;
import com.kh.spring.recommend.model.vo.Weather;
import com.kh.spring.recommend.model.vo.Recommend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.List;
import java.util.Map; 

import org.springframework.http.HttpHeaders; 
import org.springframework.http.ResponseEntity; 
import org.springframework.http.HttpMethod; 
import org.springframework.http.HttpEntity; 

@Service
public class RecommendServiceImpl implements RecommendService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private RecommendDao recommendDao;
    
    @Value("${api.weather.key}")
    private String weatherApiKey;
    
    @Value("${api.weather.url}")
    private String weatherApiUrl;
    
    @Value("${api.air.key}")
    private String airApiKey;
    
    @Value("${api.air.url}")
    private String airApiUrl;
    
    @Value("${api.kakao.key}") 
    private String kakaoApiKey; 

    @Value("${api.kakao.url}")
    private String kakaoApiUrl; 

    @Override
    public Location getLocationByCoords(double latitude, double longitude) {
        Map<String, Double> coords = new HashMap<>();
        coords.put("latitude", latitude);
        coords.put("longitude", longitude);
        Location existingLocation = recommendDao.selectLocationByCoords(coords);

        if (existingLocation != null) {
            return existingLocation;
        }

        Location newLocation = new Location();
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);

        try {
            String kakaoCoord2AddressUrl = UriComponentsBuilder.fromUriString(kakaoApiUrl)
                    .path("/geo/coord2address.json")
                    .queryParam("x", longitude)
                    .queryParam("y", latitude)
                    .toUriString();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers); 

            ResponseEntity<Map> kakaoResponse = restTemplate.exchange(
                URI.create(kakaoCoord2AddressUrl),
                HttpMethod.GET, 
                entity,
                Map.class
            );

            Map responseBody = kakaoResponse.getBody();
            if (responseBody != null && responseBody.containsKey("documents")) {
                List<Map> documents = (List<Map>) responseBody.get("documents");
                if (!documents.isEmpty()) {
                    Map document = documents.get(0);
                    Map addressInfo = (Map) document.get("address");
                    if (addressInfo != null) {
                        newLocation.setAddress((String) addressInfo.get("address_name"));
                        newLocation.setLocationName((String) addressInfo.get("region_1depth_name"));
                    } else { 
                        Map roadAddressInfo = (Map) document.get("road_address");
                        if (roadAddressInfo != null) {
                           newLocation.setAddress((String) roadAddressInfo.get("address_name"));
                           newLocation.setLocationName((String) roadAddressInfo.get("region_1depth_name"));
                        }
                    }
                }
            }
            
            Map<String, Integer> xy = convertLatLngToXY(latitude, longitude);
            newLocation.setNx(xy.get("nx"));
            newLocation.setNy(xy.get("ny"));

        } catch (Exception e) {
            System.err.println("Kakao Local API 호출 및 좌표 변환 오류: " + e.getMessage());
            e.printStackTrace();
            newLocation.setLocationName("위치 알 수 없음");
            newLocation.setAddress("위치 정보 없음");
            newLocation.setNx(60); 
            newLocation.setNy(127); 
        }
        
        recommendDao.insertLocation(newLocation);
        
        return newLocation;
    }
    
    @Override
    public Weather getWeatherInfo(Location location) {
        Weather existingWeather = recommendDao.selectWeatherByLocationNo(location.getLocationNo());

        if (existingWeather != null) {
            return existingWeather; 
        }

        Weather newWeather = new Weather();
        newWeather.setLocationNo(location.getLocationNo());
        
        try {
            LocalDateTime now = LocalDateTime.now();
            String baseDate = getBaseDate(now);
            String baseTime = getBaseTime(now);

            String weatherApiUrlFull = UriComponentsBuilder.fromUriString(weatherApiUrl)
                .path("/getVilageFcst")
                .queryParam("serviceKey", weatherApiKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 100)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", location.getNx())
                .queryParam("ny", location.getNy())
                .toUriString();

            ResponseEntity<Map> weatherApiResponse = restTemplate.getForEntity(URI.create(weatherApiUrlFull), Map.class);
            Map weatherResponseBody = weatherApiResponse.getBody();

            if (weatherResponseBody != null && weatherResponseBody.containsKey("response")) {
                Map responseMap = (Map) weatherResponseBody.get("response");
                if(responseMap.containsKey("header") && ((Map)responseMap.get("header")).containsKey("resultCode")){
                    String resultCode = (String) ((Map)responseMap.get("header")).get("resultCode");
                    String resultMsg = (String) ((Map)responseMap.get("header")).get("resultMsg");
                    if (!"00".equals(resultCode)) { 
                         throw new RuntimeException("기상청 API 오류: " + resultCode + " - " + resultMsg);
                    }
                }

                Map bodyMap = (Map) responseMap.get("body");
                if (bodyMap != null && bodyMap.containsKey("items")) {
                    Map itemsMap = (Map) bodyMap.get("items");
                    List<Map> item = (List<Map>) itemsMap.get("item");

                    Integer temp = null;
                    Integer humid = null;
                    Integer rainProb = null;
                    String weatherCond = "알 수 없음";

                    if (item != null) { 
                        for (Map<String, String> itemData : item) {
                            String category = itemData.get("category");
                            String fcstValue = itemData.get("fcstValue"); 

                            switch (category) {
                                case "TMP": 
                                    temp = (int) Double.parseDouble(fcstValue);
                                    break;
                                case "REH": 
                                    humid = (int) Double.parseDouble(fcstValue);
                                    break;
                                case "POP": 
                                    rainProb = (int) Double.parseDouble(fcstValue);
                                    break;
                                case "PTY": 
                                    if (fcstValue.equals("1") || fcstValue.equals("2") || fcstValue.equals("4")) {
                                        weatherCond = "비";
                                    } else if (fcstValue.equals("3")) {
                                        weatherCond = "눈";
                                    }
                                    break;
                                case "SKY": 
                                    if (weatherCond.equals("알 수 없음")) { 
                                        if (fcstValue.equals("1")) weatherCond = "맑음";
                                        else if (fcstValue.equals("3")) weatherCond = "구름많음";
                                        else if (fcstValue.equals("4")) weatherCond = "흐림";
                                    }
                                    break;
                            }
                        }
                    }
                    newWeather.setTemperature(temp);
                    newWeather.setHumidity(humid);
                    newWeather.setRainProbability(rainProb);
                    newWeather.setWeatherCondition(weatherCond);
                }
            }

            // 에어코리아 미세먼지 API 호출
            String airStationName = "종로"; 

            String airApiUrlFull = UriComponentsBuilder.fromUriString(airApiUrl)
                .path("/getMsrstnAcctoRltmMesureDnsty") 
                .queryParam("serviceKey", airApiKey)
                .queryParam("returnType", "json")
                .queryParam("numOfRows", 1)
                .queryParam("pageNo", 1)
                .queryParam("stationName", airStationName)
                .queryParam("dataTime", now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"))) 
                .toUriString();

            ResponseEntity<Map> airApiResponse = restTemplate.getForEntity(URI.create(airApiUrlFull), Map.class);
            Map airResponseBody = airApiResponse.getBody();

            if (airResponseBody != null && airResponseBody.containsKey("response")) {
                Map responseMap = (Map) airResponseBody.get("response");
                if(responseMap.containsKey("header") && ((Map)responseMap.get("header")).containsKey("resultCode")){
                    String resultCode = (String) ((Map)responseMap.get("header")).get("resultCode");
                    String resultMsg = (String) ((Map)responseMap.get("header")).get("resultMsg");
                    if (!"00".equals(resultCode)) { 
                         throw new RuntimeException("에어코리아 API 오류: " + resultCode + " - " + resultMsg);
                    }
                }
                Map bodyMap = (Map) responseMap.get("body");
                if (bodyMap != null && bodyMap.containsKey("items")) {
                    List<Map> item = (List<Map>) bodyMap.get("items");
                    if (!item.isEmpty()) {
                        Map airData = item.get(0);
                        String pm10Value = (String) airData.get("pm10Value");
                        String airGradeValue = (String) airData.get("pm10Grade");

                        newWeather.setPm10(pm10Value != null && !pm10Value.isEmpty() ? Integer.parseInt(pm10Value) : null);
                        newWeather.setAirGrade(airGradeValue != null && !airGradeValue.isEmpty() ? convertAirGradeToText(airGradeValue) : "알 수 없음");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("날씨/미세먼지 API 호출 오류: " + e.getMessage());
            e.printStackTrace();
            newWeather.setTemperature(null); 
            newWeather.setHumidity(null); 
            newWeather.setRainProbability(null);
            newWeather.setWeatherCondition("정보 없음");
            newWeather.setPm10(null);
            newWeather.setAirGrade("정보 없음");
        }
        
        recommendDao.insertWeather(newWeather);
        
        return newWeather;
    }
    
    @Override
    public List<Recommend> getRecommendations(Weather weather) {
        List<Recommend> recommendations = new ArrayList<>();
        
        if (weather != null && weather.getTemperature() != null && weather.getPm10() != null && weather.getRainProbability() != null) {
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
            
            if (weather.getTemperature() != null && weather.getTemperature() > 28) {
                addRecommendation(recommendations, weather.getWeatherNo(), "수영", "INDOOR");
                addRecommendation(recommendations, weather.getWeatherNo(), "실내 헬스장", "INDOOR");
            } else if (weather.getTemperature() <= 28 && weather.getTemperature() >= 5) {
                addRecommendation(recommendations, weather.getWeatherNo(), "수영", "INDOOR");
            }
            
            if (weather.getTemperature() != null && weather.getTemperature() < 5) {
                addRecommendation(recommendations, weather.getWeatherNo(), "홈트레이닝", "INDOOR");
                addRecommendation(recommendations, weather.getWeatherNo(), "탁구", "INDOOR");
            }
        } else {
            addRecommendation(recommendations, weather != null ? weather.getWeatherNo() : 0, "걷기 (정보 부족)", "OUTDOOR");
            addRecommendation(recommendations, weather != null ? weather.getWeatherNo() : 0, "집에서 운동하기", "INDOOR");
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

    @Override
    public List<Location> getAllWeatherLocations() {
        return recommendDao.selectAllWeatherLocations(); 
    }

    @Override
    public Location getWeatherAndRecommendationsByCoords(double latitude, double longitude) {
        Location location = getLocationByCoords(latitude, longitude); 

        Weather weather = getWeatherInfo(location); 

        List<Recommend> recommendations = recommendDao.selectRecommendsByWeatherNo(weather.getWeatherNo()); 
        if (recommendations == null || recommendations.isEmpty()) {
            recommendations = getRecommendations(weather); 
        }
        
        location.setWeatherObject(weather); 
        weather.setRecommendList(recommendations);

        return location;
    }

    private Map<String, Integer> convertLatLngToXY(double lat, double lng) {
        double RE = 6371.00877; 
        double GRID = 5.0; 
        double SLAT1 = 30.0; 
        double SLAT2 = 60.0; 
        double OLON = 126.0; 
        double OLAT = 38.0; 

        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = RE * sf / Math.tan(Math.PI * 0.25 + olat * 0.5);

        Map<String, Integer> result = new HashMap<>();
        double ra = RE * sf / Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        double theta = lng * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        double nx = Math.floor(ra * Math.sin(theta) + BaseX + 0.5);
        double ny = Math.floor(ro - ra * Math.cos(theta) + BaseY + 0.5);

        result.put("nx", (int) nx);
        result.put("ny", (int) ny);
        return result;
    }

    private static final int BaseX = 60; 
    private static final int BaseY = 127; 

    private String getBaseDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private String getBaseTime(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();

        if (minute < 45) { 
            hour -= 1;
            if (hour < 0) hour = 23; 
        }

        if (hour >= 23) return "2300";
        if (hour >= 20) return "2000";
        if (hour >= 17) return "1700";
        if (hour >= 14) return "1400";
        if (hour >= 11) return "1100";
        if (hour >= 8) return "0800";
        if (hour >= 5) return "0500";
        if (hour >= 2) return "0200";
        return "2300"; 
    }

    private String convertAirGradeToText(String gradeCode) {
        if (gradeCode == null) return "정보 없음";
        switch (gradeCode) {
            case "1": return "좋음";
            case "2": return "보통";
            case "3": return "나쁨";
            case "4": return "매우나쁨";
            default: return "알 수 없음";
        }
    }
    //아아
}