package com.kh.spring.habit.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.habit.model.vo.Habit;
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
		List<Habit> list = service.habitList();
		model.addAttribute("habit", list);

		return "habit/list";
	}
	
	// 등록 폼 보여주기
	@GetMapping("/goal")
	public String goal() {
		return "habit/goal";
	}
	
	
	@PostMapping("/goal")
	public String goal(HttpSession session, Model model) {
		
		
		return "habit/goal";
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

		return "redirect:/habit/list";
	}
	
	@GetMapping("/cal")
	public String habitCalendar(Model model, @SessionAttribute("loginUser") User user) {
	    List<Habit> habits = service.getHabitsByUser(user.getUserNo());
	    LocalDate today = LocalDate.now();
	    LocalDate start = today.withDayOfMonth(1);
	    LocalDate end = today.withDayOfMonth(today.lengthOfMonth());

	    model.addAttribute("habits", habits);
	    return "habit/cal";
	}

	
	
	
	 @GetMapping("/edit/{habitNo}")
	    public String showEditForm() {
	        return "habit/edit"; 
	    }
	 
	 @PostMapping("/edit/{habitNo}")
	    public String EditForm(@PathVariable int habitNo, Model model) {
	        Habit habit = service.getHabitById(habitNo);
	        model.addAttribute("habit", habit);
	        return "habit/edit"; // habit 수정용 JSP/Thymeleaf
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
	    public String deleteHabit(@PathVariable("habitNo") int habitNo, RedirectAttributes ra) {
	        service.deleteHabit(habitNo);
	        ra.addFlashAttribute("msg", "습관이 삭제되었습니다.");
	        return "redirect:/habit/list";
	    }
	    
	    
	    @GetMapping("/today")
	    public String showTodayHabits(Model model) {
	        return "habit/today"; 
	    }
	    

}
