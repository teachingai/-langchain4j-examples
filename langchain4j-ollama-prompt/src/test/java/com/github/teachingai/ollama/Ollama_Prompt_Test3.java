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

public class Ollama_Prompt_Test3 {

    public static void main(String[] args) {

        ChatLanguageModel chatLanguageModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("qwen:7b") // try "mistral", "llama2", "codellama", "phi" or "tinyllama"
                .temperature(0.9D)
                .timeout(Duration.ofSeconds(60))
                .build();

        // 系统提示消息
        SystemMessage systemMessage = new SystemMessage("你的任务是识别用户对手机流量套餐产品的选择条件。\n" +
                        "每种流量套餐产品包含三个属性：名称(name)，月费价格(price)，月流量(data)。\n" +
                        "根据用户输入，识别用户在上述三种属性上的需求是什么。\n" +
                        "以JSON格式输出。\n" +
                        "1. name字段的取值为string类型，取值必须为以下之一：经济套餐、畅游套餐、无限套餐、校园套餐 或 null；\n" +
                        "2. price字段的取值为一个结构体 或 null，包含两个字段：\n" +
                        "(1) operator, string类型，取值范围：'<='（小于等于）, '>=' (大于等于), '=='（等于）\n" +
                        "(2) value, int类型\n" +
                        "3. data字段的取值为取值为一个结构体 或 null，包含两个字段：\n" +
                        "(1) operator, string类型，取值范围：'<='（小于等于）, '>=' (大于等于), '=='（等于）\n" +
                        "(2) value, int类型或string类型，string类型只能是'无上限'\n" +
                        "4. 用户的意图可以包含按price或data排序，以sort字段标识，取值为一个结构体：\n" +
                        "(1) 结构体中以\"ordering\"=\"descend\"表示按降序排序，以\"value\"字段存储待排序的字段\n" +
                        "(2) 结构体中以\"ordering\"=\"ascend\"表示按升序排序，以\"value\"字段存储待排序的字段\n" +
                        "输出中只包含用户提及的字段，不要猜测任何用户未直接提及的字段，不输出值为null的字段。");

        String input_text = "办个100G以上的套餐";
               //input_text = "有没有便宜的套餐";
               //input_text = "有没有土豪套餐";

        UserMessage userMessage = new UserMessage(input_text);

        List<ChatMessage> messages  = List.of(systemMessage, userMessage);

        Response<AiMessage> response = chatLanguageModel.generate(messages);

        String content = response.content().text();

        System.out.println(response.content().text());

    }

}
