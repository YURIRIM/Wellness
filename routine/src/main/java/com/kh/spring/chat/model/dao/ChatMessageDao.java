package com.kh.spring.chat.model.dao;

import com.kh.spring.chat.model.vo.ChatMessage;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class ChatMessageDao {
    
    public int insertChatMessage(SqlSessionTemplate sqlSession, ChatMessage message) {
        return sqlSession.insert("chatMessageMapper.insertChatMessage", message);
    }
    
    public int updateChatMessage(SqlSessionTemplate sqlSession, ChatMessage message) {
        return sqlSession.update("chatMessageMapper.updateChatMessage", message);
    }
    
    public int deleteChatMessage(SqlSessionTemplate sqlSession, int messageNo) {
        return sqlSession.delete("chatMessageMapper.deleteChatMessage", messageNo);
    }
    
    public List<ChatMessage> selectChatMessages(SqlSessionTemplate sqlSession, Map<String, Object> params) {
        return sqlSession.selectList("chatMessageMapper.selectChatMessages", params);
    }
    
    public int selectUnreadCount(SqlSessionTemplate sqlSession, int messageNo) {
        return sqlSession.selectOne("chatMessageMapper.selectUnreadCount", messageNo);
    }
    
    public int updateReadTime(SqlSessionTemplate sqlSession, Map<String, Object> params) {
        return sqlSession.update("chatMessageMapper.updateReadTime", params);
    }
}