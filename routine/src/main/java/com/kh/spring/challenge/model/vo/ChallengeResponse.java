package com.kh.spring.challenge.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

//challenge 조회용 객체
public class ChallengeResponse {
    private int chalNo;
    private String title;
    private String content;
    private byte[] thumbnail;
    private String thumbnailBase64; //프론트와 base64로 썸네일 파일 통신
    
    private LocalDateTime createDate;
    private int verifyCycle;
    private String verifyCycleStr;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; //Y:활성화 N:종료됨 D:삭제됨
    private String pictureRequired; //I:직접 촬영한 사진 필수 Y:필수 O:선택 N:불가
    private String replyRequired; //Y:필수 O:선택 N:불가
    
    private String categoryName; //challenge_category
    private String categoryMajorName;

    private String nick; //member
    
    private byte[] picture; //profile
    private String pictureBase64;
    
    private int participateCount; //challenge_participation
    private Double successRatio; //challenge_participation, 백분율
    private Double failRatio; //challenge_participation, 백분율
    
    //-----세부 조회용 요소-----
    private int userNo;
    private int categoryNo;
    
    private int successCount; //challenge_participation
    private int failCount;
    
    private String bio; //profile
    private String isOpen; //Y:공개, N:참여한 챌린지 비공개, A:익명
    
    //이 챌린지에 현재 접속한 사람이 참여중임?
    private String loginUserIsParticipation;
    	// Y:참여중, S:성공, F:실패, N:미참여, U:로그인 안함
    
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
