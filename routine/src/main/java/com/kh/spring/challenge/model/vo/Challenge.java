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
    private String verifyCycle;
    private Timestamp startDate;
    private Timestamp endDate;
    private String status;
    private String pictureRequired;
    private String replyRequired;
    
    private String categoryName; //challenge_category

    private String nick; //member
    private String role; //member
}
