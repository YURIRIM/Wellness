package com.kh.spring.notice.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Notice {
    
    private int noticeNum;           // 공지번호 (PK)
    private String noticetitle;           // 공지 제목
    private String notice;         // 공지 내용
    private Date createdate;         // 작성일

}