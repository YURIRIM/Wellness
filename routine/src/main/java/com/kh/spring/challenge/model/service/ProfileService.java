package com.kh.spring.challenge.model.service;

import com.kh.spring.challenge.model.vo.ProfileRequest;

import jakarta.servlet.http.HttpSession;

public interface ProfileService {

	int selectMyProfile(HttpSession session) throws Exception;

	void insertMyProfile(HttpSession session, ProfileRequest p) throws Exception;

	void updateMyProfile(HttpSession session, ProfileRequest p) throws Exception;

}
