package com.kh.spring.challenge.model.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Attachment {
	private int attachmentNo;
	private int challNo;
	private int commentNo;
	private byte[] file;
	private int fileSize;
	private String fileName;
	private String status; //Y:정상, N:삭제
}
