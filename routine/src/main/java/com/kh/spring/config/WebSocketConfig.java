package com.kh.spring.config;

import com.kh.spring.chat.server.ChatWebSocketServer;
import com.kh.spring.chat.model.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Autowired
    private ChatMessageService chatMessageService;
    
    @Bean
    public WebSocketHandler chatServer() {
        ChatWebSocketServer server = new ChatWebSocketServer();
        server.setChatMessageService(chatMessageService);
        return server;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatServer(), "/chat")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }
}