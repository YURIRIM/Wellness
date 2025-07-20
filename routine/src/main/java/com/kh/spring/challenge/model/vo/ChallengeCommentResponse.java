package com.kh.spring.challenge.model.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChallengeCommentResponse {
	private int commentNo;
	private int userNo;
	private int chalNo;
	private int recommentTarget;
	private LocalDateTime createDate;
	private String reply;
	private String status; //Y:활성, N:비활성, M:수정됨

	private String nick;//member
	
	private byte[] picture; //profile
	private String pictureBase64; //프론트와 base64로 소통
	private String isOpen; //Y:공개, N:참여한 챌린지 비공개, A:익명
	
	private String hasAttachment; //attachment, 사진이 있나요? - Y:있음, N:없음
}
