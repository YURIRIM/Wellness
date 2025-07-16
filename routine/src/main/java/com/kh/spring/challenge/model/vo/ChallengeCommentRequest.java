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
	
	private byte[] uuid; //사진 연결용 uuid
	
	//프론트에서 받아오고 DB저장 안 하는 값
	private String pictureWatermark; //D:디폴트 F:개인 N:없음
}
