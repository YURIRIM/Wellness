package com.kh.spring.videoCall.model.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.common.Regexp;
import com.kh.spring.util.cryption.AESCryption;

import reactor.core.publisher.Mono;

@Service
public class DailyServiceImpl implements DailyService{
	
	//웹클라 자동생성
	public WebClient dailyWebClient() throws Exception{
		if(Regexp.dailycoApiKey==null) throw new Exception("apiKey가 없어요!");
		return WebClient.builder().baseUrl(Regexp.DAILYCO_URL).defaultHeader("Authorization", "Bearer " + Regexp.dailycoApiKey).build();
	}
	
	//apiKey활성화
	@Override
	public int activateDailCo(String keyStr) throws Exception {
		//열쇠를 받아 복호화해 되돌려주는 채신 택티껄 로-직(아님)
		Regexp.dailycoApiKey = AESCryption.decryptionFile("src/main/resources/config/daily-co-key.enc", keyStr);
		if(Regexp.dailycoApiKey==null) return 0;
		return 1;
	}
    
    //토큰 생성
	@Override
    public Mono<String> createMeetingToken(String uuidStr, User u, String roleType) throws Exception {
		
    	//호출한 유저가 주인장일 경우
    	boolean isOwner = "O".equals(roleType);
    	
    	//요청 몸매 만들기
    	Map<String, Object> requestBody = Map.of(
			"properties", Map.of(
				"room_name", uuidStr,
				"is_owner", isOwner,
				"user_name", u.getNick(),
				"enable_screenshare", true,
				"exp", System.currentTimeMillis()/1000 + 86400
			)
		);
    	
    	return dailyWebClient().post()
    			.uri("/meeting-tokens")
    			.bodyValue(requestBody)
    			.retrieve()
    			.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
    			.map(response -> (String) response.get("token"));
    }
    
    //새로운 방 생성
	@Override
    public String createRoom(String uuidStr) throws Exception{
    	
		//현재 시간 추출
		long epochSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		int expireTimestamp = (int)(epochSecond + Regexp.EXPIRETIME);

		//요청 몸매 만들기
        Map<String, Object> requestBody = Map.of(
            "name", uuidStr,
            "privacy", "private",
            "properties", Map.of(
                "exp", expireTimestamp,
                "enable_screenshare", true,
                "enable_chat", true,
                "lang", "ko",
                "max_participants", 100
            )
        );
        
        String url = dailyWebClient().post()
            .uri("/rooms")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .map(response -> (String) response.get("url")).block();
        
        return url;
    }

	//방 삭제
	@Override
	public boolean deleteRoom(String roomUuidStr) throws Exception {
        dailyWebClient()
            .delete()
            .uri("/rooms/{roomName}", roomUuidStr)
            .retrieve()
            .toBodilessEntity()
            .block();
        return true;
	}
	
	//방 참여자 수 받아오기
	@Override
	public Map<String, Integer> countParticipants() throws Exception {
	    //Presence API 호출
	    String jsonResponse = dailyWebClient()
	        .get()
	        .uri("/presence")
	        .retrieve()
	        .bodyToMono(String.class)
	        .block();

	    Map<String, Integer> result = new HashMap<>();

	    if (jsonResponse == null) throw new Exception("Presence 요청 대 실 패!!");
	    else if(jsonResponse.isEmpty()) return result;
	    	

	    //JSON 문자열 -> JsonObject
	    JsonObject jobject = JsonParser.parseString(jsonResponse).getAsJsonObject();

	    //"rooms" 키의 배열 파싱
	    JsonArray roomsArray = jobject.getAsJsonArray("rooms");

	    //각 방 객체별로 이름과 참가자 수 파싱
	    for (JsonElement roomElement : roomsArray) {
	        if (!roomElement.isJsonObject()) continue;

	        JsonObject roomObj = roomElement.getAsJsonObject();

	        String name = roomObj.has("name") ? roomObj.get("name").getAsString() : null;
	        
	        int count = 0;
	        if (roomObj.has("participants") && roomObj.get("participants").isJsonArray()) {
	            count = roomObj.getAsJsonArray("participants").size();
	        }

	        if (name != null) result.put(name, count);
	    }

	    return result;
	}

}
