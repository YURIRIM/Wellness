package com.kh.spring.challenge.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.ChallengeCommentResponse;
import com.kh.spring.challenge.model.vo.SearchComment;

@Repository
public class ChallengeCommentDao {

	public List<ChallengeCommentResponse> chalDetailComment(SqlSessionTemplate sqlSession, SearchComment sc) {
		return sqlSession.selectList("challengeCommentMapper.chalDetailComment",sc);
	}

	public List<ChallengeCommentResponse> chalDetailRecomment(SqlSessionTemplate sqlSession, SearchComment sc) {
		return sqlSession.selectList("challengeCommentMapper.chalDetailRecomment",sc);
	}

}
