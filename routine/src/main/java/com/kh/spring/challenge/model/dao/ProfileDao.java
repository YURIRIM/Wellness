package com.kh.spring.challenge.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.ProfileRequest;
import com.kh.spring.challenge.model.vo.ProfileResponse;

@Repository
public class ProfileDao {

	public ProfileResponse selectMyProfile(SqlSessionTemplate sqlSession, int userNo) {
		return sqlSession.selectOne("profileMapper.selectMyProfile", userNo);
	}

	public int insertMyProfile(SqlSessionTemplate sqlSession, ProfileRequest p) {
		return sqlSession.insert("profileMapper.insertMyProfile", p);
	}

	public int updateMyProfile(SqlSessionTemplate sqlSession, ProfileRequest p) {
		return sqlSession.update("profileMapper.updateMyProfile", p);
	}

	public String profileIsOpen(SqlSessionTemplate sqlSession, int userNo) {
		return sqlSession.selectOne("profileMapper.profileIsOpen",userNo);
	}

	public ProfileResponse profileDetail(SqlSessionTemplate sqlSession, int userNo) {
		return sqlSession.selectOne("profileMapper.profileDetail",userNo);
	}

	public ProfileResponse updateSessionMyProfile(SqlSessionTemplate sqlSession,int userNo) {
		return sqlSession.selectOne("profileMapper.updateSessionMyProfile",userNo);
	}

}
