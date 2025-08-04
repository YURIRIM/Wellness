package com.kh.spring.chat.model.service;

import com.kh.spring.chat.model.vo.ChatMessage;
import java.util.List;

public interface ChatMessageService {
    
    int insertChatMessage(ChatMessage message);
    int updateChatMessage(ChatMessage message);
    int deleteChatMessage(int messageNo);
    List<ChatMessage> selectAllChatMessages(int meetingNo);
    List<ChatMessage> selectRecentChatMessages(int meetingNo, int limit);
    int selectUnreadCount(int messageNo);
    int updateReadTime(int meetingNo, int userNo);
    int updateSenderReadTime(int messageNo);
    int sendMessage(ChatMessage message);
}	