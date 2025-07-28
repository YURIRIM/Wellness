package com.kh.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DailyCoConfig {

	private String apiUrl = "https://api.daily.co/v1/";
	public String apiKey = null;

	public WebClient dailyWebClient() {
		return WebClient.builder().baseUrl(apiUrl).defaultHeader("Authorization", "Bearer " + apiKey).build();
	}
	
}
