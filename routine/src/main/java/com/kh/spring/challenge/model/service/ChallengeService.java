package com.kh.spring.challenge.model.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import com.kh.spring.challenge.model.vo.ChallengeCategory;
import com.kh.spring.challenge.model.vo.ChallengeRequest;
import com.kh.spring.challenge.model.vo.ChallengeResponse;
import com.kh.spring.challenge.model.vo.SearchChallenge;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public interface ChallengeService {

	//새로운 챌린지 생성
	void newChal(HttpSession session, Model model, ChallengeRequest chall) throws Exception;

	//비동기 - 챌린지 메인에서 챌린지 목록 조회
	ResponseEntity<List<ChallengeResponse>> selectChal(HttpSession session, Model model, SearchChallenge sc) throws Exception;

	//controllerAdviser - CC조회
	List<ChallengeCategory> selectCCList();

	//챌린지 상세보기
	void chalDetail(HttpServletRequest request, HttpSession session, Model model, int chalNo) throws Exception;

	void chalParticipate(HttpSession session, Model model, int chalNo) throws Exception;

	void goUpdateChal(Model model, int chalNo) throws Exception;

	void deleteChal(HttpSession session, int chalNo) throws Exception;

	void closeChal(HttpSession session, int chalNo) throws Exception;

	void updateChal(HttpSession session, Model model, ChallengeRequest chal) throws Exception;
}
