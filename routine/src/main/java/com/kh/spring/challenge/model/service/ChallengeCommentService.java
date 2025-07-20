package com.kh.spring.challenge.model.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import com.kh.spring.challenge.model.vo.ChallengeCommentRequest;
import com.kh.spring.challenge.model.vo.ChallengeCommentResponse;
import com.kh.spring.challenge.model.vo.SearchComment;

import jakarta.servlet.http.HttpSession;

public interface ChallengeCommentService{

	ResponseEntity<List<ChallengeCommentResponse>> selectComment(Model model, SearchComment sc) throws Exception;

	ResponseEntity<List<ChallengeCommentResponse>> selectRecomment(Model model, SearchComment sc) throws Exception;

	void insertComment(HttpSession session, ChallengeCommentRequest ccr) throws Exception;

	void updateComment(HttpSession session, ChallengeCommentRequest ccr) throws Exception;

	void deleteComment(HttpSession session, ChallengeCommentRequest ccr) throws Exception;

}
