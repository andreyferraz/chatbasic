package com.example.chatbasic.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.chatbasic.model.Message;
import com.example.chatbasic.service.ChatService;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    private final List<Message> messages = new ArrayList<>();

    @GetMapping("/")
    public String getChatPage(Model model){
        model.addAttribute("messages", messages);
        return "chat";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam("userMessage") String userMessage, Model model){
        messages.add(new Message(userMessage, "user"));
        try{
            String botResponse = chatService.getChatGPTResponse(userMessage);
            messages.add(new Message(botResponse, "bot"));
        } catch(IOException e){
            messages.add(new Message("Erro ao processar sua solicitação.", "bot"));
        }
        model.addAttribute("messages", messages);
        return "chat";
    }
}
