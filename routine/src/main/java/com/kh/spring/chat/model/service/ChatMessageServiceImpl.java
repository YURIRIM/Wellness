package com.kh.spring.chat.model.service;

import com.kh.spring.chat.model.dao.ChatMessageDao;
import com.kh.spring.chat.model.vo.ChatMessage;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    
    @Autowired
    private SqlSessionTemplate sqlSession;
    
    @Autowired
    private ChatMessageDao chatMessageDao;
    
    @Override
    public int insertChatMessage(ChatMessage message) {
        return chatMessageDao.insertChatMessage(sqlSession, message);
    }
    
    @Override
    public int updateChatMessage(ChatMessage message) {
        return chatMessageDao.updateChatMessage(sqlSession, message);
    }
    
    @Override
    public int deleteChatMessage(int messageNo) {
        return chatMessageDao.deleteChatMessage(sqlSession, messageNo);
    }
    
    @Override
    public List<ChatMessage> selectAllChatMessages(int meetingNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("meetingNo", meetingNo);
        return chatMessageDao.selectChatMessages(sqlSession, params);
    }
    
    @Override
    public List<ChatMessage> selectRecentChatMessages(int meetingNo, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("meetingNo", meetingNo);
        params.put("limit", limit);
        return chatMessageDao.selectChatMessages(sqlSession, params);
    }
    
    @Override
    public int selectUnreadCount(int messageNo) {
        return chatMessageDao.selectUnreadCount(sqlSession, messageNo);
    }
    
    @Override
    public int updateReadTime(int meetingNo, int userNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("meetingNo", meetingNo);
        params.put("userNo", userNo);
        return chatMessageDao.updateReadTime(sqlSession, params);
    }
    
    @Override
    public int updateSenderReadTime(int messageNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("messageNo", messageNo);
        return chatMessageDao.updateReadTime(sqlSession, params);
    }
    
    @Override
    @Transactional
    public int sendMessage(ChatMessage message) {
        int result = chatMessageDao.insertChatMessage(sqlSession, message);
        
        if (result > 0) {
            updateSenderReadTime(message.getMessageNo());
        }
        
        return result;
    }
}