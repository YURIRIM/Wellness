package com.kh.spring.challenge.model.service;

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

	@Override
	//댓글 조회
	public void chalDetailComment(SearchComment sc, Model model) throws Exception {
		List<ChallengeComment> ccs = dao.chalDetailComment(sqlSession,sc);
		
		//대댓글 조회
		for(ChallengeComment cc : ccs) {
			sc.setCurrentPage(0);//페이징 초기화
			sc.setRecommentTarget(cc.getCommentNo());
			ccs.addAll(dao.chalDetailRecomment(sqlSession,sc));
		}
		
		model.addAttribute("chalDetailComment", ccs);
	}
}
