package com.github.teachingai.ollama;


import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.util.Scanner;

public class Langchain4jOllamaChatTest {

    static String MODEL_NAME = "qwen:7b"; // try "mistral", "llama2", "codellama", "phi" or "tinyllama"

    public static void main(String[] args) {

        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl(baseUrl())
                .modelName(MODEL_NAME)
                .temperature(0.9D)
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

    static String baseUrl() {
        return String.format("http://%s:%d", ollama.getHost(), ollama.getFirstMappedPort());
    }

}
