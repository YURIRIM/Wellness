package com.kh.spring.openForum.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OpenForumAttachment {
	
	private int fileId;  //FILE_ID	NUMBER	No		1	첨부파일번호
	private int postId;  //POST_ID	NUMBER	No		2	게시물번호(자유게시판)
	private String fileName; //FILE_NAME	VARCHAR2(255 BYTE)	Yes		3	파일명
	private String filePath;//FILE_PATH	VARCHAR2(255 BYTE)	Yes		4	파일경로
	private String fileType;//FILE_TYPE	VARCHAR2(50 BYTE)	Yes		5	파일형식
	private int fileSize;//FILE_SIZE	NUMBER	Yes		6	파일크기
	private Date uploadDate;//UPLOAD_DATE	DATE	Yes		7	업로드일
	
}
