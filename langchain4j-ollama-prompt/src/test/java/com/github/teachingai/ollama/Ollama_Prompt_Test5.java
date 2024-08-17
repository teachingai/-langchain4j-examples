package com.github.teachingai.ollama;

import com.alibaba.fastjson2.JSONObject;
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
import java.util.Map;

public class Ollama_Prompt_Test5 {

    private static ChatLanguageModel chatLanguageModel = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("qwen:7b") // try "mistral", "llama2", "codellama", "phi" or "tinyllama"
            .temperature(0.9D)
            .timeout(Duration.ofSeconds(60))
            .build();

    static class NLU {

        String prompt_template;
        List<ChatMessage> messages = new ArrayList<>();

        private NLU(String instruction, String output_format, String examples) {
            this.prompt_template = String.format("{instruction} \n\n {output_format} \n\n {examples} \n\n 用户输入：\n__INPUT__");
        }

        private String getCompletion(NLU self, String promptStr, String model){
            messages.add(new UserMessage(promptStr));
            Response<AiMessage> response = chatLanguageModel.generate(messages);
            String content = response.content().text();
            return content;
        }

        private String parse(NLU self, String user_input, String model){
            String promptStr = self.prompt_template.replace("__INPUT__", user_input);
            return self.getCompletion(self, promptStr, model);
        }

    }

    private class DST {

        private String update(JSONObject state, JSONObject nlu_semantics){

            if (nlu_semantics.containsKey("name")){
                state.clear();
            }
            if (nlu_semantics.containsKey("sort")){
               String slot = nlu_semantics.getJSONObject("sort").getString("value");
                if (state.containsKey(slot) && state.getJSONObject(slot).getString("operator").equals("==")){
                     state.remove(slot);
                }
            }
            for (Map.Entry<String, Object> entry : nlu_semantics.entrySet()) {
                state.put(entry.getKey(), entry.getValue());
            }
            return state.toJSONString();
        }

    }

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
                "根据对话上下文，识别用户在上述三种属性上的需求是什么。识别结果要包含整个对话的信息。\n" +
                "以JSON格式输出。\n" +
                "1. name字段的取值为string类型，取值必须为以下之一：经济套餐、畅游套餐、无限套餐、校园套餐 或 null；\n" +
                "\n" +
                "2. price字段的取值为一个结构体 或 null，包含两个字段：\n" +
                "(1) operator, string类型，取值范围：'<='（小于等于）, '>=' (大于等于), '=='（等于）\n" +
                "(2) value, int类型\n" +
                "\n" +
                "3. data字段的取值为取值为一个结构体 或 null，包含两个字段：\n" +
                "(1) operator, string类型，取值范围：'<='（小于等于）, '>=' (大于等于), '=='（等于）\n" +
                "(2) value, int类型或string类型，string类型只能是'无上限'\n" +
                "\n" +
                "4. 用户的意图可以包含按price或data排序，以sort字段标识，取值为一个结构体：\n" +
                "(1) 结构体中以\"ordering\"=\"descend\"表示按降序排序，以\"value\"字段存储待排序的字段\n" +
                "(2) 结构体中以\"ordering\"=\"ascend\"表示按升序排序，以\"value\"字段存储待排序的字段\n" +
                "\n" +
                "输出中只包含用户提及的字段，不要猜测任何用户未直接提及的字段。不要输出值为null的字段。\n" +
                "\n" +
                "客服：有什么可以帮您\n" +
                "用户：100G套餐有什么\n" +
                "\n" +
                "{\"data\":{\"operator\":\">=\",\"value\":100}}\n" +
                "\n" +
                "客服：有什么可以帮您\n" +
                "用户：100G套餐有什么\n" +
                "客服：我们现在有无限套餐，不限流量，月费300元\n" +
                "用户：太贵了，有200元以内的不\n" +
                "\n" +
                "{\"data\":{\"operator\":\">=\",\"value\":100},\"price\":{\"operator\":\"<=\",\"value\":200}}\n" +
                "\n" +
                "客服：有什么可以帮您\n" +
                "用户：便宜的套餐有什么\n" +
                "客服：我们现在有经济套餐，每月50元，10G流量\n" +
                "用户：100G以上的有什么\n" +
                "\n" +
                "{\"data\":{\"operator\":\">=\",\"value\":100},\"sort\":{\"ordering\"=\"ascend\",\"value\"=\"price\"}}\n" +
                "\n" +
                "客服：有什么可以帮您\n" +
                "用户：100G以上的套餐有什么\n" +
                "客服：我们现在有畅游套餐，流量100G，月费180元\n" +
                "用户：流量最多的呢\n" +
                "\n" +
                "{\"sort\":{\"ordering\"=\"descend\",\"value\"=\"data\"},\"data\":{\"operator\":\">=\",\"value\":100}}" );

        String input_text = "哪个便宜";
                //input_text = "无限量哪个多少钱";
                //input_text = "流量最大的多少钱";

        UserMessage userMessage = new UserMessage(input_text);

        List<ChatMessage> messages  = List.of(systemMessage,
                new AiMessage("客服：有什么可以帮您"),
                new UserMessage("用户：有什么100G以上的套餐推荐"),
                new AiMessage("客服：我们有畅游套餐和无限套餐，您有什么价格倾向吗？"),
                new UserMessage("用户：" + input_text),
                userMessage);

        Response<AiMessage> response = chatLanguageModel.generate(messages);

        String content = response.content().text();

        System.out.println(response.content().text());

    }

}
