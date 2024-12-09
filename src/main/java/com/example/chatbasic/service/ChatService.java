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
            // Verificar se a chave da API está configurada
            if (chatGPTConfig.getApiKey() == null || chatGPTConfig.getApiKey().isEmpty()) {
                throw new IllegalArgumentException("A chave da API do ChatGPT não está configurada.");
            }

            // Criar o corpo da requisição
            JSONObject json = new JSONObject();
            json.put("model", "text-davinci-003");
            json.put("prompt", prompt);
            json.put("max_tokens", 150);
            json.put("temperature", 0.7); // Opcional: Configurar criatividade da resposta

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            // Construir a requisição
            Request request = new Request.Builder()
                    .url(chatGPTConfig.getApiUrl())
                    .addHeader("Authorization", "Bearer " + chatGPTConfig.getApiKey())
                    .post(body)
                    .build();

            // Executar a requisição
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Erro na chamada da API: " + response.message());
                }

                // Processar o corpo da resposta
                String responseBodyString = response.body().string();
                JSONObject responseBody = new JSONObject(responseBodyString);

                // Verificar se "choices" existe e tem dados
                if (responseBody.has("choices") && responseBody.getJSONArray("choices").length() > 0) {
                    return responseBody.getJSONArray("choices").getJSONObject(0).getString("text").trim();
                } else {
                    throw new IOException("A resposta da API não contém o campo 'choices' esperado.");
                }
            }

        } catch (IOException e) {
            // Logar e retornar um erro genérico
            System.err.println("Erro ao chamar a API do ChatGPT: " + e.getMessage());
            return "Desculpe, ocorreu um erro ao processar sua solicitação. Tente novamente mais tarde.";
        } catch (Exception e) {
            // Tratar outros erros
            System.err.println("Erro inesperado: " + e.getMessage());
            return "Desculpe, ocorreu um erro inesperado.";
        }
    }
}
