package com.kh.spring.habit.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HabitCheck {
    private int checkNo;      // 체크 번호
    private int habitNo;      // 습관 번호
    private int status;       // 상태
    private Date checkDate;   // 체크일

}
