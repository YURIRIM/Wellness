package com.kh.spring.habit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.kh.spring.habit.model.vo.Habit;
import com.kh.spring.habit.service.HabitService;

@Controller
public class HabitController {
	
	@PostMapping("/add")
    public String insertHabit(Habit h) {
        int result = service.insertHabit(h);
        return "redirect:/habit/list";
    }

}
