package com.kh.spring.videoCall.model.service;

import java.util.Map;

import com.kh.spring.user.model.vo.User;

import reactor.core.publisher.Mono;

public interface DailyService {
	
	Mono<String> createMeetingToken(String uuidStr, User u, String roleType) throws Exception;

	int activateDailCo(String keyStr) throws Exception;

	String createRoom(String uuidStr) throws Exception;

	boolean deleteRoom(String roomUuidStr) throws Exception;

	Map<String, Integer> countParticipants() throws Exception;

}
