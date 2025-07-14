package com.kh.spring.meeting.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {
    private int meetingNo;
    private int userNo;
    private String title;
    private String description;
    private String location;
    private LocalDateTime meetingDate;
}