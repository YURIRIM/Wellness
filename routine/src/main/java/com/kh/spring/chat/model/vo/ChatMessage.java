package com.kh.spring.chat.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private int messageNo;
    private int meetingNo;
    private int userNo;
    private String messageContent;
    private String messageType;
    private String fileUrl;
    private String fileName;
    private LocalDateTime sentAt;
}