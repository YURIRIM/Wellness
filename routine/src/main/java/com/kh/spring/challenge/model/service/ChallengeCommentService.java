package com.kh.spring.challenge.model.service;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import com.kh.spring.challenge.model.vo.ChallengeCommentRequest;
import com.kh.spring.challenge.model.vo.SearchComment;

import jakarta.servlet.http.HttpSession;

public interface ChallengeCommentService{

	ResponseEntity<byte[]> selectComment(Model model, SearchComment sc) throws Exception;

	ResponseEntity<byte[]> selectRecomment(Model model, SearchComment sc) throws Exception;

	void insertComment(HttpSession session, ChallengeCommentRequest ccr) throws Exception;

	void updateComment(HttpSession session, ChallengeCommentRequest ccr) throws Exception;

	void deleteComment(HttpSession session, ChallengeCommentRequest ccr) throws Exception;

}
