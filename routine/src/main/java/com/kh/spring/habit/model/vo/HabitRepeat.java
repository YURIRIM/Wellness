package com.kh.spring.habit.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HabitRepeat {
	private int repeatNo;         // 습관 반복 번호
    private int habitNo;          // 습관 번호
    private String repeatType;    // 반복 유형
    private String weekDays;      // 요일 (문자열로 저장)
    private int dayOfMonth;       // 날짜 (1~31)
    private Date repeatTime;      // 시간
    
}
