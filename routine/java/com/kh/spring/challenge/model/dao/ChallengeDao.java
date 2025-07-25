package com.kh.spring.challenge.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.Challenge;
import com.kh.spring.challenge.model.vo.SearchChallenge;

@Repository
public class ChallengeDao {
	
	public List<Challenge> selectChal(SqlSessionTemplate sqlSession, SearchChallenge sc) {
		return sqlSession.selectList("challengeMapper.selectChal",sc);
	}

	public int newChal(SqlSessionTemplate sqlSession, Challenge chal) {
		return sqlSession.insert("challengeMapper.newChal", chal);
	}
	
}
