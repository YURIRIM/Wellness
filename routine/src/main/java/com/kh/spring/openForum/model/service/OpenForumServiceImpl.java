package com.kh.spring.openForum.model.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.openForum.model.dao.OpenForumDao;

@Service
public class OpenForumServiceImpl implements OpenForumService {

	@Autowired
	private SqlSessionTemplate sqlSession;
	
	@Autowired
	private OpenForumDao dao;
}
