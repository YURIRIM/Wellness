package com.kh.spring.habit.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.habit.model.vo.Goal;
import com.kh.spring.habit.model.vo.Habit;
import com.kh.spring.habit.model.vo.HabitCheck;
import com.kh.spring.habit.service.HabitService;
import com.kh.spring.user.model.vo.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/habit")
public class HabitController {

	@Autowired
	private HabitService service;

	@GetMapping("/list")
	public String habitList(HttpSession session, Model model) {
	    User loginUser = (User) session.getAttribute("loginUser");
	    if (loginUser == null) {
	        return "redirect:/user/login";
	    }

	    List<Goal> goals = service.findGoalsWithHabits(loginUser.getUserNo());
	    model.addAttribute("goals", goals);

	    return "habit/list";  // thymeleaf에서 goals 기반 출력
	}

	
	// 등록 폼 보여주기
	@GetMapping("/goal")
	public String goal() {
		return "habit/goal";
	}
	
	
	@PostMapping("/goal")
	public String goal(@ModelAttribute Goal goal, HttpSession session, 
	                   RedirectAttributes ra, Model model) {

	    User loginUser = (User) session.getAttribute("loginUser");

	    if (loginUser == null) {
	        ra.addFlashAttribute("message", "로그인이 필요합니다.");
	        return "redirect:/user/login";
	    }

	    goal.setUserNo(loginUser.getUserNo());

	    int result = service.insertGoal(goal);

	    if (result > 0) {
	        // 최근 등록한 목표 다시 조회 (예: goalService.selectRecentGoalByUser(loginUser.getUserNo()))
	        // FlashAttribute로 전달해야 redirect 후에도 유지됨
	        ra.addFlashAttribute("message", "목표가 등록되었습니다!");

	        return "redirect:/habit/add";  // habitAdd로 redirect
	    } else {
	        ra.addFlashAttribute("message", "등록 실패");
	        return "redirect:/habit/goal";  // 실패 시 다시 등록 페이지로 이동
	    }
	}

	
	
	// 등록 폼 보여주기
	@GetMapping("/add")
	public String showHabitAddForm(HttpSession session, Model model) {
		
	    User loginUser = (User) session.getAttribute("loginUser");

	    if (loginUser == null) {
	        return "redirect:/user/login"; 
	    }
	    List<Goal> goalList = service.selectGoalsByUser(loginUser.getUserNo());
	    model.addAttribute("goalList", goalList);
	    model.addAttribute("habit", new Habit());
	    
	    System.out.println(goalList);
	    
		return "habit/habitAdd";
	}

	// 등록 처리
	@PostMapping("/add")
	public String insertHabit(@ModelAttribute("habit") Habit habit, BindingResult result, Model model, HttpSession session) {

	    User loginUser = (User) session.getAttribute("loginUser");
	    if (loginUser == null) {
	        return "redirect:/user/login"; 
	    }

	    habit.setUserNo(loginUser.getUserNo());  // userNo 필드 반드시 세팅

	    service.insertHabit(habit);
	    
	    return "redirect:/habit/list"; // 등록 후 목록 리다이렉트
	}

	
	


	
	
	
	// GET 요청 시 수정 폼 로드 + 기존 데이터 모델에 추가
	@GetMapping("/edit/{habitNo}")
	public String showEditForm(@PathVariable int habitNo, Model model) {
	    Habit habit = service.getHabitById(habitNo);
	    model.addAttribute("habit", habit);
	    return "habit/edit";
	}

	 

	    @PostMapping("/update")
	    public String updateHabit(Habit habit, RedirectAttributes ra) {
	    	service.updateHabit(habit);
	        ra.addFlashAttribute("msg", "습관이 수정되었습니다.");
	        return "redirect:/habit/list";
	    }

	    
	    
	    
	    @GetMapping("/delete/{habitNo}") 
	    public String deleteHabit() {
	    	return "redirect:/habit/list";
	    }
	    
	    @PostMapping("/delete/{habitNo}")
	    public String deleteHabit(@PathVariable int habitNo, RedirectAttributes ra) {
	        service.deleteHabit(habitNo);
	        ra.addFlashAttribute("msg", "습관이 삭제되었습니다.");
	        return "redirect:/habit/list";
	    }
	    
	    
	    @GetMapping("/today")
	    public String showTodayHabits(Model model) {
	        return "habit/today"; 
	    }
	    
	    
	    
	 // 습관 전체 목록 → habitNo별 달력 상세로 이동 링크 필요
	    @GetMapping("/cal")
	    public String showCalendar(@SessionAttribute("loginUser") User loginUser, Model model) {
	        List<Habit> habits = service.getHabitsByUser(loginUser.getUserNo());
	        model.addAttribute("habits", habits);
	        return "habit/cal"; // 습관목록+달력보기링크
	    }

	    // 개별 습관 달력
	    @GetMapping("/cal/{habitNo}")
	    public String habitCalendar(@PathVariable int habitNo, Model model) {
	        // 습관 정보 조회
	        Habit habit = service.getHabitById(habitNo);

	        // 체크 기록 조회
	        List<HabitCheck> habitChecks = service.getChecksByHabit(habitNo);


	        model.addAttribute("habit", habit);
	        model.addAttribute("habitChecks", habitChecks);
	        return "habit/cal"; // 달력html에서 JS로 checks 사용
	    }

		
	    
	    
	    

}
