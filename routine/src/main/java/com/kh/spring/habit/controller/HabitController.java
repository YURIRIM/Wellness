package com.kh.spring.habit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.spring.habit.model.vo.Habit;
import com.kh.spring.habit.service.HabitService;
import com.kh.spring.user.model.vo.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/habit")
public class HabitController {

	@Autowired
	private HabitService service;

	@GetMapping("/habitList")
	public String habitList(HttpSession session, Model model) {
		List<Habit> list = service.habitList();
		model.addAttribute("habit", list);

		return "habit/habitList";
	}

	// 등록 폼 보여주기
	@GetMapping("/add")
	public String showHabitAddForm() {
		return "habit/habitAdd";
	}

	// 등록 처리
	@PostMapping("/add")
	public String insertHabit(@ModelAttribute Habit h, HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");
		h.setUserNo(loginUser.getUserNo());

		service.insertHabit(h);

		return "redirect:/habit/habitList";
	}

}
