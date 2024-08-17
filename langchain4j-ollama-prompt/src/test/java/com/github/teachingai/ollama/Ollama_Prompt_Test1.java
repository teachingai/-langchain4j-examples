package com.github.teachingai.ollama;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;

import java.time.Duration;
import java.util.List;

public class Ollama_Prompt_Test1 {

    public static void main(String[] args) {

        /**
         * qwen2:7b ：https://ollama.com/library/qwen2
         * gemma2:9b ：https://ollama.com/library/gemma2
         * llama3:8b ：https://ollama.com/library/llama3
         * mistral ：https://ollama.com/library/mistral
         */
        ChatLanguageModel chatLanguageModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("qwen:7b") // try "mistral", "llama2", "codellama", "phi" or "tinyllama"
                .temperature(0.9D)
                .timeout(Duration.ofSeconds(60))
                .build();

        List<ChatMessage> messages  = List.of(
                new SystemMessage("你的任务是识别用户对手机流量套餐产品的选择条件。\n" +
                        "每种流量套餐产品包含三个属性：名称，月费价格，月流量。\n" +
                        "根据用户输入，识别用户在上述三种属性上的倾向。"),
                new UserMessage("办个100G的套餐。"));

        Response<AiMessage> response = chatLanguageModel.generate(messages);

        String content = response.content().text();

        System.out.println(response.content().text());

    }

}
