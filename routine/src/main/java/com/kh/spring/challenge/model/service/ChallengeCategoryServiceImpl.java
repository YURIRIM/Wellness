package com.kh.spring.challenge.model.service;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.challenge.model.dao.ChallengeCategoryDao;
import com.kh.spring.challenge.model.vo.ChallengeCategory;

@Service
public class ChallengeCategoryServiceImpl implements ChallengeCategoryService{
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private ChallengeCategoryDao dao;
	
	//controllerAdviser - CC조회
	@Override
	public List<ChallengeCategory> selectCCList() throws Exception{
		return dao.selectCCList(sqlSession);
	}
}
