package com.kh.spring.habit.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RewardRule {
	private int ruleId;       // 기준 번호
    private int start;        // 시작 기준
    private int interval;     // 주기
    private int endAt;        // 종료 기준

}
