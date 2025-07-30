package com.kh.spring.videoCall.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VideoCallRequest {
	private byte[] roomUuid;
	private String roomUuidStr;
	
	private int chalNo;
	private String roomName;
	private String isRestrict; //N:없음, Y:참여자만, S:참여자 및 성공한 사람
	private String status; //Y:정상, N:비활성화, D:삭제됨
}
