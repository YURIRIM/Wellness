package com.kh.spring.likes.model.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.likes.model.dao.LikesDao;

@Service
public class LikesServiceImpl implements LikesService {

		@Autowired
		private LikesDao dao;
		
		@Autowired
		private SqlSessionTemplate sqlSession;
}
