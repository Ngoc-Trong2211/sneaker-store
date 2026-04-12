package com.example.sneaker_store.controller;

import com.example.sneaker_store.service.ChatService;
import com.example.sneaker_store.util.exception.user.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/chat")
    public String chat(String message) throws IdInvalidException {
        return chatService.chat(message);
    }
}
