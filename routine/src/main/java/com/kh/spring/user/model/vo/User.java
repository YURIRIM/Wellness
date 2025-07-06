package com.kh.spring.user.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
	private int userNo;
    private String userId;
    private String name;
    private String nick;
    private String password;
    private String email;
    private String role;
    private Date createdAt;
}
