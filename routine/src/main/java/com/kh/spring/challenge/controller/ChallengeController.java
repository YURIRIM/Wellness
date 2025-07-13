package com.kh.spring.challenge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.challenge.model.service.ChallengeService;
import com.kh.spring.challenge.model.vo.ChallengeRequest;
import com.kh.spring.challenge.model.vo.SearchChallenge;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/challenge")
public class ChallengeController {
	@Autowired
	private ChallengeService service;
	
	//챌린지 메인 화면으로
	@GetMapping("/chalMain")
	public String goChalMain() {
		return "/challenge/chalMain";
	}
	
	//챌린지 왼쪽 사이드바 불러오기
	@GetMapping("/chalMainLeft")
	public String getChalMainLeft() {
		return "/challenge/chalMain-left :: chalMain-left";
	}
	
	//비동기 - 챌린지 메인에서 챌린지 리스트
	@ResponseBody
	@GetMapping("/chalMainSearch")
	public String selectChalAjax(HttpSession session, Model model, SearchChallenge sc) {
		try {
			service.selectChal(session,model,sc);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	//비동기 - 왼쪽 사이드바에서 검색했을 경우
	@GetMapping("/chalMainSearchLeft")
	public String selectChal(HttpSession session, Model model, SearchChallenge sc) {
		try {
			//비동기 조회 로직과 동일
			service.selectChal(session,model,sc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//반환값은 메인 조각을 보내 처리
		return "/challenge/chalMain-center :: chalMain-center";
	}
	
	//챌린지 세부 화면으로
	@GetMapping("/chalDetail")
	public String goChalDetail() {
		return "/challenge/chalDetail :: chalDetail";
	}

	//새로운 챌린지 생성하기
	@GetMapping("/newChal")
	public String getNewChal() {
		return "/challenge/newChal :: newChal";
	}
	
	//내가 생성한 챌린지로
	@GetMapping("/createdChal")
	public String goCreatedChal() {
		return "/challenge/createdChal :: createdChal";
	}
	
	//내가 참여한 챌린지로
	@GetMapping("/joinedChal")
	public String goJoinedChal() {
		return "/challenge/joinedChal :: joinedChal";
	}
	
	//새로운 챌린지 생성하기
	@PostMapping("/newChal")
	public String newChall(HttpSession session ,Model model
			,ChallengeRequest chal, List<MultipartFile> files) {
		int chalNo=0;
		try {
			service.newChal(session, model, chal, files);
			chalNo = chal.getChalNo();
		} catch (Exception e) {
			e.printStackTrace();
			return "common/errorPage";
		}
		return "redirect:/challenge/chalDetail?chalNo="+chalNo;
	}
	
	//비동기 - 댓글 생성 중 사진 메타데이터 검증
}
