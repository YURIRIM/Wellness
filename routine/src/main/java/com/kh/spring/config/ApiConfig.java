package com.kh.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiConfig {
    
    @Value("${api.timeout.connection:5000}")
    private int connectionTimeout;
    
    @Value("${api.timeout.read:10000}")
    private int readTimeout;
    
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);  
        factory.setReadTimeout(readTimeout);           
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        return restTemplate;
    }
}