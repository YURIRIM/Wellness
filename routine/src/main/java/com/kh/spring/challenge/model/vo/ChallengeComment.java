package com.kh.spring.challenge.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChallengeComment {
	private int commentNo;
	private int userNo;
	private int chalNo;
	private int recommentTarget;
	private String reply;
	private String status;

	private String nick;//member
	private String role;//member
	
	//프론트에서 받아오고 DB저장 안 하는 값
	private String pictureWatermark; //D:디폴트 F:개인 N:없음
}
