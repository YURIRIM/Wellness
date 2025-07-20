package com.kh.spring.challenge.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.ChallengeReqired;
import com.kh.spring.challenge.model.vo.ChallengeRequest;
import com.kh.spring.challenge.model.vo.ChallengeResponse;
import com.kh.spring.challenge.model.vo.LoginUserAndChal;
import com.kh.spring.challenge.model.vo.SearchChallenge;
import com.kh.spring.challenge.model.vo.SearchMyChallenge;

@Repository
public class ChallengeDao {
	
	public List<ChallengeResponse> selectChal(SqlSessionTemplate sqlSession, SearchChallenge sc) {
		return sqlSession.selectList("challengeMapper.selectChal",sc);
	}

	public int newChal(SqlSessionTemplate sqlSession, ChallengeRequest chal) {
		return sqlSession.insert("challengeMapper.newChal", chal);
	}

	public List<ChallengeResponse> selectMyChal(SqlSessionTemplate sqlSession, SearchMyChallenge smc) {
		return sqlSession.selectList("challengeMapper.selectMyChal",smc);
	}

	public ChallengeResponse chalDetail(SqlSessionTemplate sqlSession, int chalNo) {
		return sqlSession.selectOne("challengeMapper.chalDetail", chalNo);
	}
	
	public String loginUserIsParticipant(SqlSessionTemplate sqlSession, LoginUserAndChal lac) {
		return sqlSession.selectOne("challengeMapper.loginUserIsParticipant",lac);
	}

	public int newParticipant(SqlSessionTemplate sqlSession, LoginUserAndChal lac) {
		return sqlSession.insert("challengeMapper.newParticipant",lac);
	}

	public ChallengeReqired selectRequired(SqlSessionTemplate sqlSession, int chalNo) {
		return sqlSession.selectOne("challengeMapper.selectRequired",chalNo);
	}

	public ChallengeRequest goUpdateChal(SqlSessionTemplate sqlSession, int chalNo) {
		return sqlSession.selectOne("challengeMapper.goUpdateChal",chalNo);
	}

	public int loginUserIsWriter(SqlSessionTemplate sqlSession, LoginUserAndChal lac) {
		return sqlSession.selectOne("challengeMapper.loginUserIsWriter", lac);
	}

	public int deleteChal(SqlSessionTemplate sqlSession, int chalNo) {
		return sqlSession.update("challengeMapper.deleteChal", chalNo);
	}

	public int closeChal(SqlSessionTemplate sqlSession, int chalNo) {
		return sqlSession.update("challengeMapper.closeChal", chalNo);
	}

	public int updateChal(SqlSessionTemplate sqlSession, ChallengeRequest chal) {
		return sqlSession.update("challengeMapper.updateChal", chal);
	}

}
