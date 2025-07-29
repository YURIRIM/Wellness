package com.kh.spring.challenge.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Attachment {
	private int attachmentNo;
	private int refNo;
	private String refTable;//C:챌린지, T:챌린지 썸네일, R:챌린지 댓글
	private byte[] file;
	private int fileSize;
	private String fileName;
	private String status; //Y:정상(디폴트), N:삭제
}
