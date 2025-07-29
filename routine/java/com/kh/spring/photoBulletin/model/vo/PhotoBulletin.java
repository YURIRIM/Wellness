package com.kh.spring.photoBulletin.model.vo;

import java.sql.Date;

public class PhotoBulletin {
	//사진 위주의 운동 인증 게시판 (인스타그램 같은 느낌으로) 
	//첨부파일은 사진만 가능하게, 3~5장으로 제한 후 커버 사진 설정 가능하도록
	
	private int postId;//POST_ID	NUMBER	No		1	게시물번호(운동게시판)
	private int userNo;//USER_NO	NUMBER	No		2	회원번호
	private String caption; //CAPTION	VARCHAR2(500 BYTE)	Yes		3	설명글
	private String mainImage;//MAIN_IMAGE	VARCHAR2(255 BYTE)	Yes		4	대표사진(대표주소)
	private Date createDate; //CREATED_AT	DATE	Yes		5	작성일

	//추후 추가? 첨부파일을 담아올 ArrayList 추가해야함??
}
