package com.kh.spring.openForum.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OpenForum {
//네이버 게시판 같은 내용 위주의 자유 게시판. 각종 첨부파일 첨부 가능
	
	private int postId; //POST_ID NUMBER
	private int userNo;	//USER_NO	NUMBER	No		2	회원번호
	private String title; //TITLE VARCHAR2(50 BYTE)
	private String content; //CONTENT	CLOB
	private Date createDate; //CREATED_AT	DATE

	//추후 추가? 첨부파일을 담아올 ArrayList 추가해야함
}	
	

