package com.kh.spring.videoCall.model.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VideoCallResponse {
	private byte[] roomUuid;
	private String roomUuidStr;
	
	private int chalNo;
	private int userNo;
	private String roomName;
	private LocalDateTime createDate;
	private String isRestrict; //N:없음, Y:참여자만, S:참여자 및 성공한 사람
	private String status; //Y:정상, N:비활성화, D:삭제됨-사용되지 않음
	
	private int participants;
	
    private String title; //challenge
    private String content;
    
    private String nick; //user
}
