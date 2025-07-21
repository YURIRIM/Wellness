package com.kh.spring.challenge.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
//챌린지, 챌린지 댓글의 소유권을 알아내는데 사용되는 객체
public class LoginUserAndChal {
	private int chalNo; //댓글에서 사용될 때는 commentNo
	private int userNo;
}
