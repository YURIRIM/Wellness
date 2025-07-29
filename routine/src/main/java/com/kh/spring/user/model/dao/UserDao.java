package com.kh.spring.user.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.user.model.vo.User;

@Repository
public class UserDao {
	/*
    // 로그인 기능
    User loginUser(User user);
    
    // 회원가입 기능
    int insertUser(User user);
    
    // 정보수정 메소드
    int updateUser(User user);
    
    // 회원 탈퇴 메소드
    int deleteUser(User user);


    // 아이디 중복확인
    int checkUserId(@Param("userId") String userId);*/
	
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