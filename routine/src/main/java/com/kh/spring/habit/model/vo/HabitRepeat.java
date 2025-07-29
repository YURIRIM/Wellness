package com.kh.spring.habit.model.vo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HabitRepeat {
    private int repeatNo;              // 반복 정보 식별자
    private int habitNo;               // 습관 번호 (FK)
    
    private String repeatType;         // 반복 유형: DAILY, WEEKLY, MONTHLY, YEARLY
    private int interval;              // 반복 간격: 매N일, N주마다, N개월마다 등

    // WEEKLY 관련
    private String weekDays;           // 요일 문자열: "MON,WED,FRI" 등
    private Integer nthWeek;           // 월의 N번째 주 (1~5)

    // MONTHLY 관련
    private Integer dayOfMonth;        // 매월 N일

    // YEARLY 관련
    private Integer yearlyMonth;       // 1~12
    private Integer yearlyDay;         // 1~31

    // YEARLY 대체 표현
    private Integer yearlyNthWeek;     // 예: 3째 주
    private String yearlyDayOfWeek;    // 예: THURSDAY

    private LocalTime repeatTime;      // 반복 시간
    private boolean allDay;            // 하루종일 여부
    
    public String getRepeatTimeStr() {
        return repeatTime != null ? repeatTime.format(DateTimeFormatter.ofPattern("HH:mm")) : null;
    }

    
}
