package com.kh.spring.common.advice;

import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


import jakarta.servlet.http.HttpSession;


@ControllerAdvice
public class CommonControllerAdvice {
//	@ModelAttribute("loginUser")
//	public Member addLoginUser(HttpSession session) {
//		if(session.getAttribute("loginUser") != null) {
//			return (Member) session.getAttribute("loginUser");
//		} else {
//			return null;
//		}
//	}
	
}
