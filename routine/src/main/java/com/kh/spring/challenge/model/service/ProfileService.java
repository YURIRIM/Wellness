package com.kh.spring.challenge.model.service;

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

public interface ProfileService {

	String selectMyProfile(HttpSession session) throws Exception;

}
