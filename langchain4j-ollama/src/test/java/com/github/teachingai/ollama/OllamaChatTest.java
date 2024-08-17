package com.github.teachingai.ollama;


import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.time.Duration;
import java.util.Scanner;

public class OllamaChatTest {

    public static void main(String[] args) {

        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("qwen:7b") // try "mistral", "llama2", "codellama", "phi" or "tinyllama"
                .temperature(0.9D)
                .timeout(Duration.ofSeconds(60))
                .build();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(">>> ");
            String message = scanner.nextLine();
            if (message.equals("exit")) {
                break;
            }
            String answer =  model.generate(message);
            System.out.println("<<< " + answer);
        }
    }

}
