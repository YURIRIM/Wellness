package com.kh.spring.habit.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Habit {
	 private int habitNo;            // 습관 번호 (PK)
	    private int userNo;             // 회원 번호 (FK)
	    private int goalNo;             // 목표 번호 (FK)
	    private String title;           // 습관 제목 (필요 시)
	    private String what;            // 구체적 목표 내용 (ex: 단어 암기)
	    private int amount;             // 목표 수량 (ex: 30)
	    private String unit;            // 단위 (ex: 개, 회 등)
	    private String action; 
	    private Date startDate;         // 시작일
	    private int isChallenge;        // 챌린지 여부 (0 또는 1)
	    private int totalCheck;         // 누적 체크 일수
	    private int currentCheck;       // 현재 연속 체크 일수
	    private int maxCheck;           // 최대 연속 체크 일수
	    private String status;          // 상태 (ex: 진행 중, 완료 등)
	    private Date lastCheck;         // 마지막 체크 날짜
    
    private HabitRepeat repeat;

}
