package com.kh.spring.habit.model.vo;

import java.sql.Date;

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
    private Date repeatTime;     // 종료일
    private String status;       // 상태

}
