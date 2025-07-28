package com.kh.spring.videoCall.model.service;

import com.kh.spring.user.model.vo.User;

import reactor.core.publisher.Mono;

public interface DailyService {
	
	Mono<String> createMeetingToken(String uuidStr, User u, String roleType) throws Exception;

	int activateDailCo(String keyStr) throws Exception;

	String createRoom(String uuidStr) throws Exception;

}
