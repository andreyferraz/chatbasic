package com.example.chatbasic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatGPTConfig {

    @Value("${chatgpt.api.url}")
    private String apiUrl;

    @Value("${chatgpt.api.key}")
    private String apiKey;

    public String getApiUrl(){
        return apiUrl;
    }

    public String getApiKey(){
        return apiKey;
    }
}
