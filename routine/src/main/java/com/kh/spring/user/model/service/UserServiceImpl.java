package com.kh.spring.user.model.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.user.model.dao.UserDao;
import com.kh.spring.user.model.vo.User;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    
    @Autowired
	private SqlSessionTemplate sqlSession;

    @Override
    public User loginUser(User u) {
        return userDao.loginUser(sqlSession,u);
    }

    @Override
    public int insertUser(User u) {
        return userDao.insertUser(sqlSession,u);
    }

    @Override
    public int updateUser(User u) {
        return userDao.updateUser(sqlSession,u);
    }

    @Override
    public int deleteUser(User u) {
        return userDao.deleteUser(sqlSession,u);
    }

    @Override
    public int checkUserId(String userId) {
        return userDao.checkUserId(sqlSession,userId);
    }
}