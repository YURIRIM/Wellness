package com.kh.spring.habit.model.vo;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Goal {
    private int goalNo;          // 목표 번호
    private int userNo;          // 회원 번호
    private String title;        // 주제
    private String content;      // 내용
    private Date startDate;      // 시작일
    private Date endDate;     // 종료일
    private String status;       // 상태

    private String goalCategory;
    private String whyGoal;
    private String action;
    private Integer beforeState;
    private Integer afterState;
    private String unit;
    private String deadlineType;
    private Integer deadlineValue;
    private String deadlineUnit;
    
    private List<Habit> habits;
    
}
