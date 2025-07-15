package com.kh.spring.challenge.model.service;

import org.springframework.ui.Model;

import com.kh.spring.challenge.model.vo.SearchComment;

public interface ChallengeCommentService{

	void chalDetailComment(SearchComment sc, Model model) throws Exception;

}
