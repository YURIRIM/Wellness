package com.kh.spring.likes.model.vo;

import java.sql.Date;

public class Likes {

	private int likeId; //LIKE_ID	NUMBER	No		1	추천 식별 번호
	private int userNo; //USER_NO	NUMBER	No		2	회원번호
	private String targetType; //TARGET_TYPE	VARCHAR2(255 BYTE)	Yes		3	추천 대상 타입
	private int targetId; //TARGET_ID	NUMBER	Yes		4	추천 대상 번호
	private String bulletinType;//BULLETIN_TYPE	VARCHAR2(20 BYTE)	Yes		5	추천 대상 게시판
	private Date  createDate;//CREATED_AT	DATE	Yes		6	추천날짜

}
