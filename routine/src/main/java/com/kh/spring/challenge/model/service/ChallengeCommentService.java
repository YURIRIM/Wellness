package com.kh.spring.challenge.model.service;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import com.kh.spring.challenge.model.vo.SearchComment;

public interface ChallengeCommentService{

	ResponseEntity<byte[]> selectComment(Model model, SearchComment sc) throws Exception;

	ResponseEntity<byte[]> selectRecomment(Model model, SearchComment sc) throws Exception;

}
