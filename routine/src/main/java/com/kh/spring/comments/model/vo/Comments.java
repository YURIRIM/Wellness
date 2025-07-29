package com.kh.spring.comments.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comments {

	private int commentId; //COMMENT_ID	NUMBER	No		1	댓글 식별자
	private int postId; //POST_ID	NUMBER	No		2	게시물번호(자유게시판)
	private int userNo;//USER_NO	NUMBER	No		3	회원번호
	private String userId;
	private String content; //CONTENT	VARCHAR2(50 BYTE)	Yes		4	댓글 내용
	private int likeCount; //LIKE_COUNT	NUMBER	Yes		5	
	private Date createDate;//CREATED_AT	DATE	Yes		6	
	private String status;
}
  