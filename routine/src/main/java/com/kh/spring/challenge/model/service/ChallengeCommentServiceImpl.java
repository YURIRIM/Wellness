package com.kh.spring.challenge.model.service;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.kh.spring.challenge.model.dao.ChallengeCommentDao;
import com.kh.spring.challenge.model.vo.ChallengeComment;
import com.kh.spring.challenge.model.vo.SearchComment;

@Service
public class ChallengeCommentServiceImpl implements ChallengeCommentService{
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private ChallengeCommentDao dao;

	//챌린지 세부보기에서 댓글 리스트
	@Override
	public void chalDetailComment(int chalNo, Model model) throws Exception {
		List<ChallengeComment> ccs = new ArrayList<>();
		SearchComment sc = SearchComment.builder()
				.chalNo(chalNo).currentPage(0).build();
		ccs = dao.chalDetailComment(sqlSession,sc);
		model.addAttribute("chalDetailComment", ccs);
	}
}
