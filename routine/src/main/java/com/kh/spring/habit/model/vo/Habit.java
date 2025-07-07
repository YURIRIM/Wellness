package com.kh.spring.habit.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Habit {
    private int habitNo;            // 습관 번호
    private int userNo;             // 회원번호
    private String title;           // 주제
    private Date startDate;         // 시작일
    private int isChallenge;        // 챌린지 여부 (0 또는 1)
    private int totalCheck;         // 누적 일수
    private int currentCheck;       // 현재 연속 일수
    private int maxCheck;           // 최대 연속 일수
    private String status;          // 상태
    private Date field;             // 마지막 체크

}
