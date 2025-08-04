package com.kh.spring.user.model.service;

import com.kh.spring.user.model.vo.User;

public interface UserService {

	
    // 로그인 기능 (SELECT)
    User loginUser(User u);
    
    // 회원가입 기능 (INSERT)
    int insertUser(User u);
    
    // 회원 정보 수정 기능 (UPDATE)
    int updateUser(User u);

    // 회원 탈퇴 기능 (UPDATE)
    int deleteUser(User u);

    // 아이디 중복확인 기능
    int checkUserId(String userId);
}