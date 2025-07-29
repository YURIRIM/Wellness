package com.kh.spring.challenge.model.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import com.kh.spring.challenge.model.vo.ChallengeRequest;
import com.kh.spring.challenge.model.vo.ChallengeResponse;
import com.kh.spring.challenge.model.vo.SearchChallenge;
import com.kh.spring.challenge.model.vo.SearchMyChallenge;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public interface ChallengeService {
	void newChal(HttpSession session, Model model, ChallengeRequest chall) throws Exception;

	ResponseEntity<List<ChallengeResponse>> selectChal(HttpSession session, Model model, SearchChallenge sc) throws Exception;

	void chalDetail(HttpServletRequest request, HttpSession session, Model model, int chalNo) throws Exception;

	void goUpdateChal(Model model, int chalNo) throws Exception;

	void deleteChal(HttpSession session, int chalNo) throws Exception;

	void closeChal(HttpSession session, int chalNo) throws Exception;

	ResponseEntity<Void> updateChal(HttpSession session, ChallengeRequest chal) throws Exception;

	void challengeResponseSanitizer(List<ChallengeResponse> result) throws Exception;

	ResponseEntity<List<ChallengeResponse>> selectMyChal(HttpSession session, Model model, SearchMyChallenge smc)
			throws Exception;
}
