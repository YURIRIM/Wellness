package com.kh.spring.challenge.model.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.Challenge;

@Repository
public class ChallengeDao {

	public int newChal(SqlSessionTemplate sqlSession, Challenge chal) {
		return sqlSession.insert("challengeMapper.newChal", chal);
	}

	public List<Challenge> selectChal(SqlSessionTemplate sqlSession, Map<String, Object> map) {
		return sqlSession.selectList("challengeMapper.selectChal",map);
	}
	
}
