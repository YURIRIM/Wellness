package com.kh.spring.challenge.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.Challenge;

@Repository
public class ChallengeDao {

	public int newChal(SqlSessionTemplate sqlSession, Challenge chal) {
		return sqlSession.insert("challengeMapper.newChal", chal);
	}
	
}
