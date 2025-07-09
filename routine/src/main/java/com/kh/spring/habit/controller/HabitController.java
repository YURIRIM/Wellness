package com.kh.spring.habit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.spring.habit.model.vo.Habit;
import com.kh.spring.habit.service.HabitService;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/habit")
public class HabitController {
	
	@Autowired
	private HabitService service;
	
	@GetMapping("/list")
    public String habitList(HttpSession session,Model model) {
        List<Habit> list = service.habitList();
        model.addAttribute("habit", list);
        
        return "habit/habitList";
    }
	
	@GetMapping("/add")
    public String insertHabit(Habit h) {
//        int result = service.insertHabit(h);
        return "habit/habitAdd";
    }

}
