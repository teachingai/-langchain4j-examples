package com.github.hiwepy.huggingface.controller;

import org.springframework.ai.huggingface.HuggingfaceChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    private final HuggingfaceChatClient chatClient;

    @Autowired
    public ChatController(HuggingfaceChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/v1/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatClient.call(message));
    }

}
