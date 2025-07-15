package com.kh.spring.chat.server;

import com.kh.spring.chat.model.service.ChatMessageService;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ChatWebSocketServer extends TextWebSocketHandler {
    
    private ChatMessageService chatMessageService;
    
    private Map<Integer, Set<WebSocketSession>> meetingRooms = new ConcurrentHashMap<>();
    
    private Map<WebSocketSession, Integer> sessionMeetingMap = new ConcurrentHashMap<>();
    private Map<WebSocketSession, Integer> sessionUserMap = new ConcurrentHashMap<>();
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    public void setChatMessageService(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("채팅 접속: " + session.getId());
        
        String query = session.getUri().getQuery();
        int meetingNo = extractParameter(query, "meetingNo", 1);
        int userNo = extractParameter(query, "userNo", 1);
        
        meetingRooms.computeIfAbsent(meetingNo, k -> new CopyOnWriteArraySet<>()).add(session);
        sessionMeetingMap.put(session, meetingNo);
        sessionUserMap.put(session, userNo);
        
        System.out.println("모임 " + meetingNo + "번 방 접속자 수: " + meetingRooms.get(meetingNo).size());
        
        if (chatMessageService != null) {
            chatMessageService.updateReadTime(meetingNo, userNo);
        }
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        
        Integer meetingNo = sessionMeetingMap.get(session);
        Integer userNo = sessionUserMap.get(session);
        
        if (meetingNo == null || userNo == null) return;
        
        String payload = message.getPayload();
        
        try {
            if (payload.startsWith("{")) {
                Map<String, Object> messageData = objectMapper.readValue(payload, Map.class);
                String type = (String) messageData.get("type");
                
                if ("READ_UPDATE".equals(type)) {
                    handleReadUpdate(meetingNo, userNo);
                } else {
                    handleFileMessage(messageData, meetingNo, userNo);
                }
            } else {
                handleTextMessage(payload, meetingNo, userNo);
            }
            
        } catch (Exception e) {
            System.err.println("메시지 처리 오류: " + e.getMessage());
        }
    }
    
    private void handleTextMessage(String messageContent, int meetingNo, int userNo) throws Exception {
        if (chatMessageService == null) return;
        
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMeetingNo(meetingNo);
        chatMessage.setUserNo(userNo);
        chatMessage.setMessageContent(messageContent);
        chatMessage.setMessageType("text");
        
        int result = chatMessageService.sendMessage(chatMessage);
        
        if (result > 0) {
            int unreadCount = chatMessageService.selectUnreadCount(chatMessage.getMessageNo());
            
            Map<String, Object> messageData = new ConcurrentHashMap<>();
            messageData.put("messageNo", chatMessage.getMessageNo());
            messageData.put("messageContent", messageContent);
            messageData.put("messageType", "text");
            messageData.put("userNo", userNo);
            messageData.put("unreadCount", unreadCount);
            messageData.put("timestamp", new SimpleDateFormat("[HH:mm:ss]").format(new Date()));
            
            String jsonMessage = objectMapper.writeValueAsString(messageData);
            broadcastToRoom(meetingNo, new TextMessage(jsonMessage));
        }
    }
    
    private void handleFileMessage(Map<String, Object> messageData, int meetingNo, int userNo) throws Exception {
        if (chatMessageService == null) return;
        
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMeetingNo(meetingNo);
        chatMessage.setUserNo(userNo);
        chatMessage.setMessageContent((String) messageData.get("messageContent"));
        chatMessage.setMessageType((String) messageData.get("messageType"));
        chatMessage.setFileUrl((String) messageData.get("fileUrl"));
        chatMessage.setFileName((String) messageData.get("fileName"));
        
        int result = chatMessageService.sendMessage(chatMessage);
        
        if (result > 0) {
            int unreadCount = chatMessageService.selectUnreadCount(chatMessage.getMessageNo());
            
            messageData.put("messageNo", chatMessage.getMessageNo());
            messageData.put("unreadCount", unreadCount);
            messageData.put("timestamp", new SimpleDateFormat("[HH:mm:ss]").format(new Date()));
            
            String jsonMessage = objectMapper.writeValueAsString(messageData);
            broadcastToRoom(meetingNo, new TextMessage(jsonMessage));
        }
    }
    
    private void handleReadUpdate(int meetingNo, int userNo) {
        if (chatMessageService != null) {
            chatMessageService.updateReadTime(meetingNo, userNo);
            
            try {
                Map<String, Object> readUpdate = new ConcurrentHashMap<>();
                readUpdate.put("type", "READ_UPDATE");
                readUpdate.put("meetingNo", meetingNo);
                
                String jsonMessage = objectMapper.writeValueAsString(readUpdate);
                broadcastToRoom(meetingNo, new TextMessage(jsonMessage));
                
            } catch (Exception e) {
                System.err.println("읽음 상태 브로드캐스팅 오류: " + e.getMessage());
            }
        }
    }
    
    private void broadcastToRoom(int meetingNo, TextMessage message) throws Exception {
        Set<WebSocketSession> roomSessions = meetingRooms.get(meetingNo);
        if (roomSessions != null) {
            for (WebSocketSession session : roomSessions) {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            }
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("채팅 접속종료: " + session.getId());
        
        Integer meetingNo = sessionMeetingMap.get(session);
        if (meetingNo != null) {
            Set<WebSocketSession> roomSessions = meetingRooms.get(meetingNo);
            if (roomSessions != null) {
                roomSessions.remove(session);
                if (roomSessions.isEmpty()) {
                    meetingRooms.remove(meetingNo);
                }
            }
        }
        
        sessionMeetingMap.remove(session);
        sessionUserMap.remove(session);
    }
    
    private int extractParameter(String query, String paramName, int defaultValue) {
        if (query == null) return defaultValue;
        
        String[] params = query.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && paramName.equals(keyValue[0])) {
                try {
                    return Integer.parseInt(keyValue[1]);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
        }
        return defaultValue;
    }
}