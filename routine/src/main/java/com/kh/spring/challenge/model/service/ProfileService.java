package com.kh.spring.challenge.model.service;

import com.kh.spring.challenge.model.vo.Profile;

import jakarta.servlet.http.HttpSession;

public interface ProfileService {

	String selectMyProfile(HttpSession session) throws Exception;

	void insertMyProfile(HttpSession session, Profile p) throws Exception;

	void updateMyProfile(HttpSession session, Profile p) throws Exception;

}
