package com.kh.spring.challenge.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProfileResponse {
	private int userNo;
	private byte[] picture;
	private String pictureBase64; //프론트와 base64로 소통
	private String bio;
	private String watermarkType; //D:디폴트 F:개인 N:없음
	private byte[] watermark;
	private String watermarkBase64; //프론트와 base64로 소통
	private String isOpen; //Y:공개, N:참여한 챌린지 비공개, A:익명
	
	//challenge_participation
	private int chalParticiapteCount;
	private int successCount;
	private int failCount;
	private Double successRatio;
	private Double failRatio;
}
