package com.github.teachingai.ollama;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Ollama_Prompt_Test8 {

    private static List<ChatMessage> messages = new ArrayList<>();

    private static String instruction = "给定一段用户与手机流量套餐客服的对话，。\n" +
            "你的任务是判断客服介绍产品信息的准确性：\n" +
            "\n" +
            "当向用户介绍流量套餐产品时，客服人员必须准确提及产品名称、月费价格和月流量总量。上述信息缺失一项或多项，或信息与实时不符，都算信息不准确\n" +
            "\n" +
            "已知产品包括：\n" +
            "\n" +
            "经济套餐：月费50元，月流量10G\n" +
            "畅游套餐：月费180元，月流量100G\n" +
            "无限套餐：月费300元，月流量1000G\n" +
            "校园套餐：月费150元，月流量200G，限在校学生办理\n";

    /**
    static {
        messages.add(new SystemMessage("你是一个手机流量套餐的客服代表，你叫小瓜。可以帮助用户选择最合适的流量套餐产品，你没有办理业务的能力。可以选择的套餐包括：\n" +
                "经济套餐，月费50元，10G流量；\n" +
                "畅游套餐，月费180元，100G流量；\n" +
                "无限套餐，月费300元，1000G流量；\n" +
                "校园套餐，月费150元，200G流量，仅限在校生。"));
    }*/

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

        String output_format = "如果信息准确，输出：Y\n" +
                "如果信息不准确，输出：N\n";

        String context = "用户：你们有什么流量大的套餐\n" +
                "客服：您好，我们现在正在推广无限套餐，每月300元就可以享受1000G流量，您感兴趣吗？\n";

        String cot = "请一步一步分析以下对话后，输出Y或N\n";

        PromptTemplate promptTemplate = new PromptTemplate("{instruction} \n\n {output_format} \n\n {cot} \n\n 对话记录：{context}");
       // Prompt prompt = promptTemplate.create(Map.of("instruction", instruction, "output_format", output_format, "cot", cot, "context", context));
        Prompt prompt = promptTemplate.apply(Map.of("instruction", instruction, "output_format", output_format, "cot", cot, "context", context));

        getCompletion(chatLanguageModel, prompt.text(), "qwen2:7b");

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
