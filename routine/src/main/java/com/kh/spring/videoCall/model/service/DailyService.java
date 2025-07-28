package com.kh.spring.videoCall.model.service;

import com.kh.spring.user.model.vo.User;
import com.kh.spring.videoCall.model.vo.VideoCallRequest;

import reactor.core.publisher.Mono;

public interface DailyService {
	
	Mono<String> createMeetingToken(String uuidStr, User u, String roleType) throws Exception;

	String createRoom(String uuidStr, VideoCallRequest vc) throws Exception;

	int activateDailCo(String keyStr) throws Exception;

}
