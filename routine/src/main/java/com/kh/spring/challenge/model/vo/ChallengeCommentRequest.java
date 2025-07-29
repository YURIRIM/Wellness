package com.kh.spring.challenge.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChallengeCommentRequest {
	private int commentNo;
	private int userNo;
	private int chalNo;
	private int recommentTarget;
	private String reply;
	
	private String uuidStr;
	//사진 연결용 uuid. oracle에 넣을 때는 byte[]로 변환 필수
}
