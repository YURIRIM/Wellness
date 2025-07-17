package com.kh.spring.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.spring.challenge.model.service.ChallengeService;
import com.kh.spring.challenge.model.vo.ChallengeRequest;
import com.kh.spring.challenge.model.vo.SearchChallenge;
import com.kh.spring.challenge.model.vo.SearchMyChallenge;

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

	//새로운 챌린지 생성하기
	@GetMapping("/newChal")
	public String getNewChal() {
		return "/challenge/newChal :: newChal";
	}
	
	//내가 생성 혹은 참여한 챌린지로 이동
	@GetMapping("/goMyChal")
	@ResponseBody
	public String goMyChal(HttpSession session, Model model, String searchType) {
		try {
			SearchMyChallenge smc = SearchMyChallenge.builder()
					.currentPage(0)
					.searchType(searchType)
					.build();
			service.myChal(session,model,smc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/challenge/chalMain-center :: chalMain-center";
	}
	
	//비동기 - 내가 생성 혹은 참여한 챌린지
	@GetMapping("/myChal")
	@ResponseBody
	public String myChal(HttpSession session, Model model, SearchMyChallenge smc) {
		try {
			service.myChal(session,model,smc);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "fail";
	}
	
	
	//새로운 챌린지 생성하기
	@PostMapping("/newChal")
	public String newChall(HttpSession session ,Model model
			,ChallengeRequest chal) {
		int chalNo=0;
		try {
			service.newChal(session, model, chal);
			chalNo = chal.getChalNo();
		} catch (Exception e) {
			e.printStackTrace();
			return "common/errorPage";
		}
		return "redirect:/challenge/chalDetail?chalNo="+chalNo;
	}
	
	//챌린지 세부 화면으로
	@GetMapping("/chalDetail")
	public String goChalDetail(HttpSession session, Model model, int chalNo) {
		try {
			service.chalDetail(session, model, chalNo);
		} catch (Exception e) {
			e.printStackTrace();
			return "/common/errorPage";
		}
		return "/challenge/chalDetail";
	}	
	
	//비동기 - 챌린지 참여하기
	@PostMapping("/participate")
	public String chalParticipate(HttpSession session, Model model, int chalNo) {
		try {
			service.chalParticipate(session, model, chalNo);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	//챌린지 수정 화면으로
	@GetMapping("/updateChal")
	public String goUpdateChal(Model model, int chalNo) {
		try {
			service.goUpdateChal(model,chalNo);
			return "challenge/updateChal";
		} catch (Exception e) {
			e.printStackTrace();
			return "common/errorPage";
		}
	}
	
	//챌린지 수정
	@ResponseBody
	@PostMapping("/updateChal")
	public String updateChal(HttpSession session, Model model
			,ChallengeRequest chal) {
		try {
			service.updateChal(session, model, chal);
			
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		
	}
	
	
	//비동기 - 챌린지 삭제
	@ResponseBody
	@GetMapping("/deleteChal")
	public String deleteChal(HttpSession session, int chalNo) {
		try {
			service.deleteChal(session, chalNo);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	//비동기 - 챌린지 종료
	@ResponseBody
	@GetMapping("/closeChal")
	public String closeChal(HttpSession session, int chalNo) {
		try {
			service.closeChal(session,chalNo);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
}
