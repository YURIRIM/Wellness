package com.kh.spring.challenge.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.ChallengeCommentRequest;
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

	public int insertComment(SqlSessionTemplate sqlSession, ChallengeCommentRequest ccr) {
		return sqlSession.insert("challengeCommentMapper.insertComment",ccr);
	}
	
	public int updateComment(SqlSessionTemplate sqlSession, ChallengeCommentRequest ccr) {
		return sqlSession.update("challengeCommentMapper.updateComment",ccr);
	}

	public int deleteComment(SqlSessionTemplate sqlSession, ChallengeCommentRequest ccr) {
		return sqlSession.update("challengeCommentMapper.deleteComment",ccr);
	}

}
