package com.kh.spring.user.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
	private int userNo;//유저번호
    private String userId;//아이디
    private String name;//이름
    private String nick;//닉네임
    private String password;//비밀번호
    private String email;//이메일
    private String role; //권한
    private Date createdAt; //생성날짜
    private String status; //유저상태
    
}
