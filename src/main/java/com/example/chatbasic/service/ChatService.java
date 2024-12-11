package com.example.chatbasic.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.example.chatbasic.config.ChatGPTConfig;

import org.json.JSONObject;

import okhttp3.*;

@Service
public class ChatService {

    private final ChatGPTConfig chatGPTConfig;

    // Configuração de tempo limite
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    public ChatService(ChatGPTConfig chatGPTConfig) {
        this.chatGPTConfig = chatGPTConfig;
    }

    public String getChatGPTResponse(String prompt) {
        try {
            if (chatGPTConfig.getApiKey() == null || chatGPTConfig.getApiKey().isEmpty()) {
                throw new IllegalArgumentException("A chave da API do ChatGPT não está configurada.");
            }
    
            JSONObject json = new JSONObject();
            json.put("model", "gpt-3.5-turbo-instruct");
            json.put("prompt", prompt);
            json.put("max_tokens", 500);
            json.put("temperature", 0.7);
    
            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );
    
            Request request = new Request.Builder()
                    .url(chatGPTConfig.getApiUrl())
                    .addHeader("Authorization", "Bearer " + chatGPTConfig.getApiKey())
                    .post(body)
                    .build();
    
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Erro na chamada da API: " + response.message() + " - " + response.body().string());
                }
    
                String responseBodyString = response.body().string();
                System.out.println("Resposta da API: " + responseBodyString); // Adicione este log para verificar a resposta completa
    
                JSONObject responseBody = new JSONObject(responseBodyString);
    
                if (responseBody.has("choices") && responseBody.getJSONArray("choices").length() > 0) {
                    return responseBody.getJSONArray("choices").getJSONObject(0).getString("text").trim();
                } else {
                    throw new IOException("A resposta da API não contém o campo 'choices' esperado.");
                }
            }
    
        } catch (IOException e) {
            System.err.println("Erro ao chamar a API do ChatGPT: " + e.getMessage());
            return "Desculpe, ocorreu um erro ao processar sua solicitação. Tente novamente mais tarde.";
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
            return "Desculpe, ocorreu um erro inesperado.";
        }
    }
       
}
