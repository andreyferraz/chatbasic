package com.example.chatbasic.controller;

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

    // Lista de mensagens inicializada por requisição (melhor para múltiplos usuários)
    private final List<Message> messages = new ArrayList<>();

    @GetMapping("/")
    public String getChatPage(Model model) {
        model.addAttribute("messages", messages);
        return "chat";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam("userMessage") String userMessage, Model model) {
        // Verificar se a mensagem do usuário está vazia
        if (userMessage == null || userMessage.trim().isEmpty()) {
            messages.add(new Message("Por favor, insira uma mensagem antes de enviar.", "bot"));
            model.addAttribute("messages", messages);
            return "chat";
        }

        // Adicionar mensagem do usuário à lista
        messages.add(new Message(userMessage, "user"));

        try {
            // Chamar o serviço para obter resposta do ChatGPT
            String botResponse = chatService.getChatGPTResponse(userMessage);
            messages.add(new Message(botResponse, "bot"));
        } catch (Exception e) {
            // Log de erro para diagnóstico
            System.err.println("Erro ao processar a mensagem no ChatService: " + e.getMessage());
            // Adicionar resposta genérica em caso de falha
            messages.add(new Message("Desculpe, não consegui processar sua solicitação. Tente novamente mais tarde.", "bot"));
        }

        // Adicionar mensagens ao modelo
        model.addAttribute("messages", messages);
        return "chat";
    }
}
