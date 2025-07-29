package com.kh.spring.photoBulletin.model.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.photoBulletin.model.dao.PhotoBulletinDao;

@Service
public class PhotoBulletinServiceImpl implements PhotoBulletinService{

		@Autowired
		private SqlSessionTemplate sqlSesion;
		
		@Autowired
		private PhotoBulletinDao dao;
}
