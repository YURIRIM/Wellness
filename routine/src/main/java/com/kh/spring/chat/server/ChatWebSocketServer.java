package com.kh.spring.chat.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.spring.chat.model.service.ChatMessageService;
import com.kh.spring.chat.model.vo.ChatMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ChatWebSocketServer extends TextWebSocketHandler {

    private ChatMessageService chatMessageService;

    private final Map<Integer, Set<WebSocketSession>> meetingRooms = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Integer> sessionMeetingMap = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Integer> sessionUserMap = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> sessionUserNameMap = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void setChatMessageService(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        int meetingNo = extractParameter(query, "meetingNo", -1);
        int userNo = extractParameter(query, "userNo", -1);
        String userName = extractStringParameter(query, "userName", "알 수 없음");

        if (meetingNo == -1 || userNo == -1) {
            session.close(CloseStatus.BAD_DATA.withReason("Invalid meetingNo or userNo"));
            return;
        }

        meetingRooms.computeIfAbsent(meetingNo, k -> new CopyOnWriteArraySet<>()).add(session);
        sessionMeetingMap.put(session, meetingNo);
        sessionUserMap.put(session, userNo);
        sessionUserNameMap.put(session, userName);

        System.out.println("모임 " + meetingNo + "번 방에 '" + userName + "'님 접속. 현재 접속자 수: " + meetingRooms.get(meetingNo).size());

        if (chatMessageService != null) {
            chatMessageService.updateReadTime(meetingNo, userNo);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Integer meetingNo = sessionMeetingMap.get(session);
        Integer userNo = sessionUserMap.get(session);
        String userName = sessionUserNameMap.get(session);

        if (meetingNo == null || userNo == null || userName == null) return;

        String payload = message.getPayload();
        if (payload.startsWith("{")) {
            Map<String, String> fileData = objectMapper.readValue(payload, new TypeReference<>() {});
            handleFileMessage(fileData, meetingNo, userNo, userName);
        } else {
            handleTextMessage(payload, meetingNo, userNo, userName);
        }
    }

    private void handleTextMessage(String messageContent, int meetingNo, int userNo, String userName) throws Exception {
        if (chatMessageService == null) return;

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMeetingNo(meetingNo);
        chatMessage.setUserNo(userNo);
        chatMessage.setUserName(userName);
        chatMessage.setMessageContent(messageContent);
        chatMessage.setMessageType("text");

        chatMessageService.sendMessage(chatMessage);

        Map<String, Object> messageData = new ConcurrentHashMap<>();
        messageData.put("userNo", userNo);
        messageData.put("userName", userName);
        messageData.put("messageContent", messageContent);
        messageData.put("messageType", "text");
        messageData.put("timestamp", new SimpleDateFormat("HH:mm").format(new Date()));

        String jsonMessage = objectMapper.writeValueAsString(messageData);
        broadcastToRoom(meetingNo, new TextMessage(jsonMessage));
    }
    
    private void handleFileMessage(Map<String, String> fileData, int meetingNo, int userNo, String userName) throws Exception {
        if (chatMessageService == null) return;

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMeetingNo(meetingNo);
        chatMessage.setUserNo(userNo);
        chatMessage.setUserName(userName);
        chatMessage.setMessageType(fileData.get("messageType"));
        chatMessage.setFileName(fileData.get("fileName"));
        chatMessage.setFileUrl(fileData.get("fileUrl"));

        chatMessageService.sendMessage(chatMessage);
        
        Map<String, Object> messageData = new ConcurrentHashMap<>();
        messageData.put("userNo", userNo);
        messageData.put("userName", userName);
        messageData.put("messageType", fileData.get("messageType"));
        messageData.put("fileName", fileData.get("fileName"));
        messageData.put("fileUrl", fileData.get("fileUrl"));
        messageData.put("timestamp", new SimpleDateFormat("HH:mm").format(new Date()));

        String jsonMessage = objectMapper.writeValueAsString(messageData);
        broadcastToRoom(meetingNo, new TextMessage(jsonMessage));
    }

    private void broadcastToRoom(int meetingNo, TextMessage message) throws Exception {
        Set<WebSocketSession> roomSessions = meetingRooms.get(meetingNo);
        if (roomSessions != null) {
            for (WebSocketSession s : roomSessions) {
                if (s.isOpen()) {
                    s.sendMessage(message);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer meetingNo = sessionMeetingMap.get(session);
        String userName = sessionUserNameMap.get(session);
        
        System.out.println("채팅 접속종료: " + (userName != null ? userName : session.getId()));

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
        sessionUserNameMap.remove(session);
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

    private String extractStringParameter(String query, String paramName, String defaultValue) {
        if (query == null) return defaultValue;
        String[] params = query.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && paramName.equals(keyValue[0])) {
                return URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            }
        }
        return defaultValue;
    }
}