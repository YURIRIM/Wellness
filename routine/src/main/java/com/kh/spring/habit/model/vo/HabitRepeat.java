package com.kh.spring.habit.model.vo;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HabitRepeat {
    private int repeatId;               // repeat_id: PK
    private int habitNo;                // habit_no: FK (습관번호)
    private String repeatType;          // repeat_type: 반복 유형
    private int interval;           // interval: 반복 간격(숫자)
    private String  nthWeeks;          // 체크박스 배열
    private String  weekDaysInterval;  // 체크박스 배열
    private String  weekDays;          // 체크박스 배열
    private int allDay;
    private String repeatTime;

}
