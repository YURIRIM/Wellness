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
	/*
	 * mybatis에서는 byte[]형식 단일 파라미터에서 해당 요소 이름 못 쓴다
	 * #{array}로 접근해야함 (#{value}도 못 씀)
	 * 물론 객체/컬랙션으로 넘기면 쓸 수 있음(권장)
	 */
	private byte[] uuid;
	private int refNo;
	private String refTable;//C:챌린지, R:챌린지 댓글
	private byte[] fileContent;
	private int fileSize;
	private String fileName;
	private String status; //Y:정상, N:비활성화
}
