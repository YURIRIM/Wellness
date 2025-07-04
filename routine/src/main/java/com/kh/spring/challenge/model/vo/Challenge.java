package com.kh.spring.challenge.model.vo;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Challenge {
    private int chalNo;
    private int userNo;
    private int categoryNo;
    private String title;
    private String content;
    private Timestamp createDate;
    private int verifyCycle;
    private Timestamp startDate;
    private Timestamp endDate;
    private String status; //Y:활성화 N:종료됨 D:삭제됨
    private String pictureRequired; //Y:필수 O:선택 N:불가
    private String replyRequired; //Y:필수 O:선택 N:불가
    
    private String categoryName; //challenge_category

    private String nick; //member
    private String role; //member
    
    private String verifyCycleName; //verify_cycle
    
    /*
     * verifyCycle 인증 주기
     * 0 : 없음
     * 1~127 : 특정 요일 선택(비트마스크)
     * -월요일 : 1, 화요일 : 2, ..., 일요일 : 64
     * 
     * 201 : 매일
     * 202 : 이틀
     * 203 : 사흘
     * 211 : 매주
     * 212 : 2주
     * 221 : 매달
     * 
     */
}
