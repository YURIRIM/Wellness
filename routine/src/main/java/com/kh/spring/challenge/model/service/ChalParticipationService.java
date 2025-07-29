package com.kh.spring.challenge.model.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.kh.spring.challenge.model.vo.ChallengeResponse;

import jakarta.servlet.http.HttpSession;

public interface ChalParticipationService {

	void insertParticipant(HttpSession session, int chalNo) throws Exception;

	ResponseEntity<List<ChallengeResponse>> updateParticipant(HttpSession session, int chalNo, String type) throws Exception;
	
}
