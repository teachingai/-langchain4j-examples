package com.github.teachingai.ollama;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Ollama_Prompt_Test7 {

    private static List<ChatMessage> messages = new ArrayList<>();

    static {
        messages.add(new SystemMessage("你是一个手机流量套餐的客服代表，你叫小瓜。可以帮助用户选择最合适的流量套餐产品，你没有办理业务的能力。可以选择的套餐包括：\n" +
                "经济套餐，月费50元，10G流量；\n" +
                "畅游套餐，月费180元，100G流量；\n" +
                "无限套餐，月费300元，1000G流量；\n" +
                "校园套餐，月费150元，200G流量，仅限在校生。"));
    }

    private static String getCompletion(ChatLanguageModel chatLanguageModel, String promptStr, String model){

        messages.add(new UserMessage(promptStr));

        Response<AiMessage> response = chatLanguageModel.generate(messages);

        String content = response.content().text();

        messages.add(new AiMessage(content));

        return content;
    }

    public static void main(String[] args) {

        ChatLanguageModel chatLanguageModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("qwen:7b") // try "mistral", "llama2", "codellama", "phi" or "tinyllama"
                .temperature(0.9D)
                .timeout(Duration.ofSeconds(60))
                .build();

        getCompletion(chatLanguageModel, "流量最大的套餐是什么？", "qwen2:7b");
        getCompletion(chatLanguageModel, "多少钱？", "qwen2:7b");
        getCompletion(chatLanguageModel, "给我办一个", "qwen2:7b");

        for (ChatMessage message : messages) {
            if(message instanceof SystemMessage systemMessage){
                System.out.println(systemMessage.text());
            } else if(message instanceof UserMessage userMessage){
                System.out.println(userMessage.text());
            } else if(message instanceof AiMessage aiMessage){
                System.out.println(aiMessage.text());
            }
        }

    }

}
