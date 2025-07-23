package com.kh.spring.challenge.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.LoginUserAndChal;

@Repository
public class ChalParticipationDao {
	
	public String loginUserIsParticipant(SqlSessionTemplate sqlSession, LoginUserAndChal lac) {
		return sqlSession.selectOne("challengeParticipationMapper.loginUserIsParticipant", lac);
	}
	
	public String chalIsOpen(SqlSessionTemplate sqlSession, int chalNo) {
		return sqlSession.selectOne("challengeParticipationMapper.chalIsOpen", chalNo);
	}

	public int insertParticipant(SqlSessionTemplate sqlSession, LoginUserAndChal lac) {
		return sqlSession.insert("challengeParticipationMapper.insertParticipant", lac);
	}

	public int updateParticipant(SqlSessionTemplate sqlSession, LoginUserAndChal lac) {
		return sqlSession.update("challengeParticipationMapper.updateParticipant", lac);
	}
	
	
}
