package com.kh.spring.challenge.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.ChallengeCategory;

@Repository
public class ChallengeCategoryDao {
	
	public List<ChallengeCategory> selectCCList(SqlSessionTemplate sqlSession) {
		return sqlSession.selectList("challengeCategoryMapper.selectCCList");
	}

	public int updateEmbedding(SqlSessionTemplate sqlSession, ChallengeCategory cce) {
		return sqlSession.update("challengeCategoryMapper.updateEmbedding", cce);
	}

	public List<ChallengeCategory> selectEmbeddings(SqlSessionTemplate sqlSession) {
		return sqlSession.selectList("challengeCategoryMapper.selectEmbeddings");
	}
}
