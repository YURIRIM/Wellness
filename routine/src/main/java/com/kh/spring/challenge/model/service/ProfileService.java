package com.kh.spring.challenge.model.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.kh.spring.challenge.model.vo.ChallengeResponse;
import com.kh.spring.challenge.model.vo.ProfileRequest;
import com.kh.spring.challenge.model.vo.ProfileResponse;

import jakarta.servlet.http.HttpSession;

public interface ProfileService {

	int selectMyProfile(HttpSession session) throws Exception;

	void insertMyProfile(HttpSession session, ProfileRequest p) throws Exception;

	void updateMyProfile(HttpSession session, ProfileRequest p) throws Exception;

	ResponseEntity<ProfileResponse> profileDetail(int userNo) throws Exception;
	
	void updateSessionMyProfile(HttpSession session) throws Exception;

	ResponseEntity<List<ChallengeResponse>> chalParticipate(int userNo, int currentPage, String type) throws Exception;
}
