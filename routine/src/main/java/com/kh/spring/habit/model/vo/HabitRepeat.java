package com.kh.spring.habit.model.vo;

import java.time.LocalTime;
import java.util.List;

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
    private List<String> weekDays;  // 요일 리스트
    private int dayOfMonth;       // 날짜 (1~31)
    private LocalTime repeatTime; // 시간 (시간 정보 포함)
}
