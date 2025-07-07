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
}
