package com.kh.spring.videoCall.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoomStatus {
	private byte[] roomUuid;
	private String status;
}
