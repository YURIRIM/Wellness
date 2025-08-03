package com.kh.spring.user.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.user.model.vo.User;

@Repository
public class UserDao {

	
	public User loginUser(SqlSessionTemplate sqlSession, User u) {
		
		User loginUser = sqlSession.selectOne("userMapper.loginUser", u);
		
		return loginUser;
	}
	
	public int insertUser (SqlSessionTemplate sqlSession,User u) {
		
		return sqlSession.insert("userMapper.insertUser",u);
		
	}
		
	public int updateUser (SqlSessionTemplate sqlSession,User u) {
			
			return sqlSession.update("userMapper.updateUser",u);
			
		}
	public int deleteUser (SqlSessionTemplate sqlSession,User u) {
		
		return sqlSession.delete("userMapper.deleteUser",u);
		
	}
	public int checkUserId (SqlSessionTemplate sqlSession,String userId) {
		
		return sqlSession.selectOne("userMapper.checkUserId",userId);
		
	}
}