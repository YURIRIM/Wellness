package com.kh.spring.habit.model.vo;

import java.sql.Date;

public class StreakReward {
    private int habitNo;         // 습관 번호
    private int streakDay;       // 스트릭 일수
    private int habitNo2;        // 관련 습관 번호 (중복 관계일 경우)
    private Date givenAt;        // 보상일

}
