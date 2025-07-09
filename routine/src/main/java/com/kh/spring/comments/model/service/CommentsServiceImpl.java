package com.kh.spring.comments.model.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.comments.model.dao.CommentsDao;

@Service
public class CommentsServiceImpl implements CommentsService {
	
	@Autowired 
	private SqlSessionTemplate sqlSession;

	@Autowired
	private CommentsDao dao;
	
}
