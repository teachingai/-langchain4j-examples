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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Ollama_Prompt_Test11 {

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
                .modelName("qwen:7b") // try "mistral", "llama2", "codellama", "phi" or "tinyllama"
                .temperature(0.9D)
                .timeout(Duration.ofSeconds(60))
                .build();

        Resource systemResource = new ClassPathResource("prompts/system-message.st");
        String systemPrompt =  systemResource.getContentAsString(StandardCharsets.UTF_8);

        PromptTemplate systemPromptTemplate = new PromptTemplate("{prompt}");
        Prompt prompt = systemPromptTemplate.apply(Map.of("prompt", systemPrompt));

        String input_text = "小明本学期表现数据：{\n" +
                "  \"基础信息\": {\n" +
                "      \"学校代码\": \"2133000640\",\n" +
                "      \"学校名称\": \"中泰中心小学\",\n" +
                "      \"学校地址\": \"杭州市，余杭区\",\n" +
                "      \"学校性质\": \"小学\",\n" +
                "      \"年级名称\": \"一年级\",\n" +
                "      \"班级名称\": \"01班\",\n" +
                "      \"学生名称\": \"小明\",\n" +
                "  }，\n" +
                "  \"过程评价\": {\n" +
                "      \"学校排名\": \"-3\",\n" +
                "      \"年级排名\": \"-3\",\n" +
                "      \"班级排名\": \"-3\",\n" +
                "      \"获得荣誉\": [\"2023年第一学期三好学生\",\"书香小院士\",\"五竹少年\"],\n" +
                "      \"点评项得分\": {\n" +
                "         \"点评项1\": \"+15\",\n" +
                "         \"点评项2\": \"-3\",\n" +
                "      }\n" +
                "  }\n" +
                "  \"结果评价\": {\n" +
                "      \"品德表现\": {\n" +
                "         \"会合作\": \"A\",\n" +
                "         \"善交流\": \"A\",\n" +
                "         \"守规则\": \"A\",\n" +
                "         \"有爱心\": \"A\",\n" +
                "         \"负责任\": \"A\",\n" +
                "         \"讲文明\": \"A\",\n" +
                "         \"有礼貌\": \"A\",\n" +
                "     }，\n" +
                "     \"学业水平\": {\n" +
                "        \"道德与法治\": {\"认知水平\":\"A\",\"实践能力\":\"A\"},\n" +
                "        \"语文\": {\"识字与写字\":\"A\",\"阅读与鉴赏\":\"A\",\"表达与交流\":\"A\",\"梳理与探究\":\"A\"},\n" +
                "        \"数学\": {\"数与代数\":\"A\",\"空间与图形\":\"A\",\"统计与概率\":\"A\",\"综合与实践\":\"A\"},\n" +
                "        \"英语\": {\"视听理解\":\"A\",\"阅读理解\":\"A\",\"口头表达\":\"A\",\"书面表达\":\"A\",\"综合语言实践\":\"A\"},\n" +
                "        \"科学\": {\"科学观念\":\"A\",\"科学思维\":\"A\",\"探究实践\":\"A\"}\n" +
                "     },\n" +
                "     \"运动健康\": {\"体育与健康\":\"A\",\"体质健康测试\":\"A\",\"运动技能\":\"A\"},\n" +
                "     \"艺术素养\": {\"音乐\":\"A\",\"美术\":\"A\",\"艺术技能\":\"A\"},\n" +
                "     \"其他\": {\n" +
                "         \"信息科技\":\"A\",\n" +
                "         \"劳动\":\"A\",\n" +
                "         \"综合实践活动\":\"A\",\n" +
                "         \"地方课程\":\"A\",\n" +
                "         \"校本课程\":\"A\",\n" +
                "         \"跨学科研究性学习\":\"A\"\n" +
                "     }\n" +
                "  },\n" +
                "  \"非纸笔测评\": {\n" +
                "      \"品德表现\": \"A\",\n" +
                "  }\n" +
                "}";

        List<ChatMessage> messages  = List.of(prompt.toSystemMessage(), new UserMessage(input_text), new UserMessage("请一步步的分析前面给的数据，写200字关于小明的综合评价评语，不需要分析过程，只需要返回评语"));

        Response<AiMessage> response = chatLanguageModel.generate(messages);

        String content = response.content().text();

        messages.add(new AiMessage(content));

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
