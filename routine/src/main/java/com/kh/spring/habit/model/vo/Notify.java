package com.kh.spring.habit.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Notify {
	private int notifyNo;        // 알림 번호
    private int userNo;          // 회원 번호
    private String message;      // 메시지
    private Date createdAt;      // 생성일
    private Date scheduleTime;   // 예정 시간
	
}
