package com.kh.spring.meeting.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.kh.spring.recommend.model.vo.Location; 

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {
    private int meetingNo;
    private int userNo;
    private String title;
    private String description;
    private Integer locationNo; 
    private String addressDetail; 
    private Double latitude;      
    private Double longitude;     
    private LocalDateTime meetingDate;
    private Location locationObject; 
    private String creatorName;
}