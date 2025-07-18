package com.kh.spring.meeting.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingPart {
    private int meetingNo;
    private int userNo;
    private LocalDateTime joinedAt;
    private LocalDateTime lastReadAt; 
}