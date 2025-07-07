package com.kh.spring.challenge.model.service;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.challenge.model.vo.Challenge;

import jakarta.servlet.http.HttpSession;

public interface ChallengeService {

	//새로운 챌린지 생성
	void newChal(HttpSession session, Model model, Challenge chall, List<MultipartFile> files) throws Exception;

}
