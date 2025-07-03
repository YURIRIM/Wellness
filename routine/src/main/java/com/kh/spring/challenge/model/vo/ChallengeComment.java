package com.kh.spring.challenge.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChallengeComment {
	private Long commentNo;
	private Long userNo;
	private Long chalNo;
	private Long recommentTarget;
	private String reply;
	private String status;

	private String nick;//member
	private String role;//member
}
