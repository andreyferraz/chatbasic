package com.example.chatbasic.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.example.chatbasic.config.ChatGPTConfig;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class ChatService {

    private final ChatGPTConfig chatGPTConfig;

    public ChatService(ChatGPTConfig chatGPTConfig){
        this.chatGPTConfig = chatGPTConfig;
    }

    public String getChatGPTResponse(String prompt) throws IOException{
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("model", "text-davinci-003");
        json.put("prompt", prompt);
        json.put("max_tokens", 150);

        RequestBody body = RequestBody.create(
            json.toString(),
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(chatGPTConfig.getApiUrl())
                .addHeader("Authorization", "Bearer " + chatGPTConfig.getApiKey())
                .post(body) 
                .build();
                
                try(Response response = client.newCall(request).execute()){
                    if(!response.isSuccessful()){
                        throw new IOException("Erro na chamada da API" + response.message());
                    }
                    JSONObject responseBody = new JSONObject(response.body().string());
                    return responseBody.getJSONArray("choices").getJSONObject(0).getString("text").trim();
                }

    }
}
