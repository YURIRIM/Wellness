package com.kh.spring.challenge.model.service;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.challenge.model.vo.ChallengeRequest;
import com.kh.spring.challenge.model.vo.ChallengeCategory;
import com.kh.spring.challenge.model.vo.SearchChallenge;

import jakarta.servlet.http.HttpSession;

public interface ChallengeService {

	//새로운 챌린지 생성
	void newChal(HttpSession session, Model model, ChallengeRequest chall, List<MultipartFile> files) throws Exception;

	//비동기 - 챌린지 메인에서 챌린지 목록 조회
	void selectChal(HttpSession session, Model model, SearchChallenge sc) throws Exception;

	//controllerAdviser - CC조회
	List<ChallengeCategory> selectCCList();

}
