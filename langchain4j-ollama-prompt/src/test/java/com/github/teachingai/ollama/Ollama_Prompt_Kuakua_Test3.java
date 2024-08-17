package com.github.teachingai.ollama;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 基于 自定义提示词 模型的测试
 */
public class Ollama_Prompt_Kuakua_Test3 {

    /**
     * qwen2:7b ：https://ollama.com/library/qwen2
     * gemma2:9b ：https://ollama.com/library/gemma2
     * glm4:9b ：https://ollama.com/library/glm4
     * llama3:8b ：https://ollama.com/library/llama3
     * mistral ：https://ollama.com/library/mistral
     */
    public static void main(String[] args) throws IOException {

        ChatLanguageModel chatLanguageModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("qwen2-7b-kuakua") // try "mistral", "llama2", "codellama", "phi" or "tinyllama"
                .temperature(0.9D)
                .timeout(Duration.ofSeconds(60))
                .build();

        List<ChatMessage> historyList = new ArrayList<>();
        String firstText = "今天工作很累呢～";
        System.out.println("<<< " + firstText);
        // 用户输入消息
        historyList.add(new UserMessage(firstText));
        // 生成对话
        Scanner scanner = new Scanner(System.in);
        while (true) {

            Flux<ChatResponse> chatResponse = chatLanguageModel.generate(historyList);
            System.out.print(">>> ");
            StringBuilder sb = new StringBuilder();
            chatResponse.doOnNext(response -> {
                historyList.add(response.getResult().getOutput());
                String resp = response.getResult().getOutput().getContent();
                System.out.print(resp);
                sb.append(resp);
            }).blockLast();
            historyList.add(new AssistantMessage(sb.toString()));
            System.out.println("");
            System.out.print("<<< ");
            String message = scanner.nextLine();
            if (message.equals("exit")) {
                break;
            }
            historyList.add(new UserMessage(message));
        }
    }

}
