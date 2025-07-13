package com.kh.spring.challenge.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.Profile;

@Repository
public class ProfileDao {

	public Profile selectMyProfile(SqlSessionTemplate sqlSession, int userNo) {
		return sqlSession.selectOne("profileMapper.selectMyProfile", userNo);
	}

}
