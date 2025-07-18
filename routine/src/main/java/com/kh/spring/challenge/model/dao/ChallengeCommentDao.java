package com.kh.spring.challenge.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.ChallengeComment;
import com.kh.spring.challenge.model.vo.SearchComment;

@Repository
public class ChallengeCommentDao {

	public List<ChallengeComment> chalDetailComment(SqlSessionTemplate sqlSession, SearchComment sc) {
		return sqlSession.selectList("challengeCommentMapper.chalDetailComment",sc);
	}

	public List<ChallengeComment> chalDetailRecomment(SqlSessionTemplate sqlSession, SearchComment sc) {
		return sqlSession.selectList("challengeCommentMapper.chalDetailRecomment",sc);
	}

}
