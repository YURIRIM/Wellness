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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            HttpEntity<String> entity = new HttpEntity<>(headers);

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
             
            if (newLocation.getNy() <= 0) {
                newLocation.setNy(127);
            }

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
        // =================================================================
        // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 수정된 부분 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
        // newLocation.setLocationNo(location.getLocationNo()); -> newWeather.setLocationNo(location.getLocationNo());
        newWeather.setLocationNo(location.getLocationNo());
        // =================================================================
        // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲ 수정된 부분 ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

        try {
            LocalDateTime now = LocalDateTime.now();
            String baseDate = getBaseDate(now);
            String baseTime = getBaseTime(now);

            String encodedKey = URLEncoder.encode(weatherApiKey, StandardCharsets.UTF_8);
            String weatherUrl = String.format("%s/getVilageFcst?serviceKey=%s&pageNo=1&numOfRows=100&dataType=JSON&base_date=%s&base_time=%s&nx=%d&ny=%d",
                    weatherApiUrl, encodedKey, baseDate, baseTime, location.getNx(), location.getNy());
            URI weatherUri = URI.create(weatherUrl);

            System.out.println("기상청 API URL: " + weatherUri);

            ResponseEntity<Map> weatherApiResponse = restTemplate.getForEntity(weatherUri, Map.class);
            Map weatherResponseBody = weatherApiResponse.getBody();
            System.out.println("기상청 API 응답 본문: " + weatherResponseBody);

            if (weatherResponseBody != null && weatherResponseBody.containsKey("response")) {
                Map responseMap = (Map) weatherResponseBody.get("response");
                if(responseMap.containsKey("header")){
                    Map headerMap = (Map) responseMap.get("header");
                    String resultCode = (String) headerMap.get("resultCode");
                    if (!"00".equals(resultCode)) {
                        System.err.println("기상청 API 오류: " + resultCode + " - " + headerMap.get("resultMsg"));
                    } else {
                        Map bodyMap = (Map) responseMap.get("body");
                        if (bodyMap != null && bodyMap.containsKey("items")) {
                            Map itemsMap = (Map) bodyMap.get("items");
                            List<Map<String, String>> item = (List<Map<String, String>>) itemsMap.get("item");

                            if (item != null) {
                                for (Map<String, String> itemData : item) {
                                    String category = itemData.get("category");
                                    String fcstValue = itemData.get("fcstValue");

                                    if (fcstValue != null) {
                                        switch (category) {
                                            case "TMP": newWeather.setTemperature((int) Double.parseDouble(fcstValue)); break;
                                            case "REH": newWeather.setHumidity((int) Double.parseDouble(fcstValue)); break;
                                            case "POP": newWeather.setRainProbability((int) Double.parseDouble(fcstValue)); break;
                                            case "PTY":
                                                if ("1".equals(fcstValue) || "2".equals(fcstValue) || "4".equals(fcstValue)) {
                                                    newWeather.setWeatherCondition("비");
                                                } else if ("3".equals(fcstValue)) {
                                                    newWeather.setWeatherCondition("눈");
                                                }
                                                break;
                                            case "SKY":
                                                if (newWeather.getWeatherCondition() == null || "알 수 없음".equals(newWeather.getWeatherCondition())) {
                                                    if ("1".equals(fcstValue)) newWeather.setWeatherCondition("맑음");
                                                    else if ("3".equals(fcstValue)) newWeather.setWeatherCondition("구름많음");
                                                    else if ("4".equals(fcstValue)) newWeather.setWeatherCondition("흐림");
                                                }
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("날씨 API 호출 오류: " + e.getMessage());
            e.printStackTrace();
            newWeather.setTemperature(null);
            newWeather.setHumidity(null);
            newWeather.setRainProbability(null);
            newWeather.setWeatherCondition("정보 없음");
        }

        try {
            String stationName = getNearbyStationName(location.getLatitude(), location.getLongitude());
            
            if (stationName == null) {
                stationName = "종로구";
            }
            
            String encodedKey = URLEncoder.encode(airApiKey, StandardCharsets.UTF_8);
            String encodedStationName = URLEncoder.encode(stationName, StandardCharsets.UTF_8);
            
            String airUrl = String.format(
                    "%s/getMsrstnAcctoRltmMesureDnsty?serviceKey=%s&returnType=json&numOfRows=1&pageNo=1&stationName=%s&dataTerm=DAILY&ver=1.0",
                    airApiUrl, 
                    encodedKey, 
                    encodedStationName
            );

            URI airUri = URI.create(airUrl);

            System.out.println("에어코리아 API URL: " + airUri);

            ResponseEntity<Map> airApiResponse = restTemplate.getForEntity(airUri, Map.class);
            Map airResponseBody = airApiResponse.getBody();
            System.out.println("에어코리아 API 응답 본문: " + airResponseBody);

            if (airResponseBody != null && airResponseBody.containsKey("response")) {
                Map responseMap = (Map) airResponseBody.get("response");

                if(responseMap.containsKey("header")){
                    Map headerMap = (Map) responseMap.get("header");
                    String resultCode = (String) headerMap.get("resultCode");
                    if (!"00".equals(resultCode)) {
                        System.err.println("에어코리아 API 오류: " + resultCode + " - " + headerMap.get("resultMsg"));
                    } else {
                        Map bodyMap = (Map) responseMap.get("body");
                        if (bodyMap != null && bodyMap.containsKey("items") && bodyMap.get("totalCount") != null && (Integer)bodyMap.get("totalCount") > 0) {
                            List<Map> item = (List<Map>) bodyMap.get("items");
                            if (!item.isEmpty()) {
                                Map airData = item.get(0);
                                String pm10Value = (String) airData.get("pm10Value");
                                String airGradeValue = (String) airData.get("pm10Grade");

                                newWeather.setPm10(pm10Value != null && !"-".equals(pm10Value) ? Integer.parseInt(pm10Value) : null);
                                newWeather.setAirGrade(airGradeValue != null && !"-".equals(airGradeValue) ? convertAirGradeToText(airGradeValue) : "알 수 없음");
                            }
                        } else {
                             System.err.println("에어코리아 API 응답에 유효한 미세먼지 데이터가 없습니다.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("미세먼지 API 호출 오류: " + e.getMessage());
            e.printStackTrace();
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
            boolean isGoodForOutdoor = (weather.getTemperature() >= 5 && weather.getTemperature() <= 30)
                                    && (weather.getPm10() <= 80)
                                    && (weather.getRainProbability() <= 50);
             
            if (isGoodForOutdoor) {
                recommendations.add(createRecommend(weather.getWeatherNo(), "조깅", "OUTDOOR"));
                recommendations.add(createRecommend(weather.getWeatherNo(), "축구", "OUTDOOR"));
                recommendations.add(createRecommend(weather.getWeatherNo(), "농구", "OUTDOOR"));
                recommendations.add(createRecommend(weather.getWeatherNo(), "자전거", "OUTDOOR"));
            }

            recommendations.add(createRecommend(weather.getWeatherNo(), "헬스장", "INDOOR"));
            recommendations.add(createRecommend(weather.getWeatherNo(), "복싱", "INDOOR"));
            recommendations.add(createRecommend(weather.getWeatherNo(), "수영", "INDOOR"));
             
            if (weather.getTemperature() > 28) {
                recommendations.add(createRecommend(weather.getWeatherNo(), "실내 헬스장", "INDOOR"));
            } else if (weather.getTemperature() < 5) {
                recommendations.add(createRecommend(weather.getWeatherNo(), "홈트레이닝", "INDOOR"));
                recommendations.add(createRecommend(weather.getWeatherNo(), "탁구", "INDOOR"));
            }
        } else {
            recommendations.add(createRecommend(weather != null ? weather.getWeatherNo() : 0, "걷기", "OUTDOOR"));
            recommendations.add(createRecommend(weather != null ? weather.getWeatherNo() : 0, "집에서 운동하기", "INDOOR"));
        }

        if (!recommendations.isEmpty()) {
            for (Recommend recommend : recommendations) {
                recommendDao.insertRecommend(recommend);
            }
        }

        return recommendations;
    }
     
    private Recommend createRecommend(int weatherNo, String exerciseType, String locationType) {
        Recommend recommend = new Recommend();
        recommend.setWeatherNo(weatherNo);
        recommend.setExerciseType(exerciseType);
        recommend.setLocationType(locationType);
        return recommend;
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
    
    private Map<String, Double> convertWGS84ToTM(double longitude, double latitude) {
        try {
            String transcoordUrl = UriComponentsBuilder.fromUriString(kakaoApiUrl)
                    .path("/geo/transcoord.json")
                    .queryParam("x", longitude)
                    .queryParam("y", latitude)
                    .queryParam("input_coord", "WGS84")
                    .queryParam("output_coord", "TM")
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                URI.create(transcoordUrl), HttpMethod.GET, entity, Map.class
            );

            Map responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("documents")) {
                List<Map> documents = (List<Map>) responseBody.get("documents");
                if (!documents.isEmpty()) {
                    Map<String, Object> coordsDoc = documents.get(0);
                    Map<String, Double> tmCoords = new HashMap<>();
                    tmCoords.put("tmX", (Double) coordsDoc.get("x"));
                    tmCoords.put("tmY", (Double) coordsDoc.get("y"));
                    return tmCoords;
                }
            }
        } catch (Exception e) {
            System.err.println("Kakao 좌표 변환 API 호출 오류: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    private String getNearbyStationName(double latitude, double longitude) {
        Map<String, Double> tmCoords = convertWGS84ToTM(longitude, latitude);
        if (tmCoords == null) {
            System.err.println("TM 좌표 변환 실패. 기본 측정소 '종로구'를 사용합니다.");
            return "종로구";
        }

        try {
            String encodedKey = URLEncoder.encode(airApiKey, StandardCharsets.UTF_8);
            
            String nearbyStationUrl = String.format(
                "%s/getNearbyMsrstnList?serviceKey=%s&returnType=json&tmX=%f&tmY=%f",
                airApiUrl, encodedKey, tmCoords.get("tmX"), tmCoords.get("tmY")
            );
            
            URI nearbyStationUri = URI.create(nearbyStationUrl);
            
            System.out.println("근접 측정소 API URL: " + nearbyStationUri);

            ResponseEntity<Map> response = restTemplate.getForEntity(nearbyStationUri, Map.class);
            Map responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("response")) {
                Map responseMap = (Map) responseBody.get("response");
                if (responseMap.containsKey("body")) {
                    Map bodyMap = (Map) responseMap.get("body");
                    if (bodyMap.containsKey("items") && bodyMap.get("items") instanceof List) {
                        List<Map> items = (List<Map>) bodyMap.get("items");
                        if (!items.isEmpty()) {
                            Map<String, Object> firstStation = items.get(0);
                            String stationName = (String) firstStation.get("stationName");
                            System.out.println("가장 가까운 측정소: " + stationName);
                            return stationName;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("근접 측정소 API 호출 오류: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.err.println("근접 측정소 API 조회 실패. 기본 측정소 '종로구'를 사용합니다.");
        return "종로구";
    }

    private Map<String, Integer> convertLatLngToXY(double lat, double lng) {
        double RE = 6371.00877;
        double GRID = 5.0;
        double SLAT1 = 30.0;
        double SLAT2 = 60.0;
        double OLON = 126.0;
        double OLAT = 38.0;

        double DEGRAD = Math.PI / 180.0;
        
        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = re * sf / Math.tan(Math.PI * 0.25 + olat * 0.5);

        double ra = re * sf / Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        double theta = lng * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int x = (int) Math.floor(ra * Math.sin(theta) + 1.5);
        int y = (int) Math.floor(ro - ra * Math.cos(theta) + 1.5);

        Map<String, Integer> result = new HashMap<>();
        result.put("nx", x);
        result.put("ny", y);
        return result;
    }

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
}