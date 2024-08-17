package com.github.teachingai.ollama.controller;

import com.github.teachingai.ollama.request.ApiRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    private final ChatLanguageModel chatLanguageModel;
    private final StreamingChatLanguageModel streamingChatLanguageModel;
    private final List<ToolSpecification> toolSpecifications;

    @Autowired
    public ChatController(ChatLanguageModel chatLanguageModel,
                          StreamingChatLanguageModel streamingChatLanguageModel,
                          List<ToolSpecification> toolSpecifications) {
        this.chatLanguageModel = chatLanguageModel;
        this.streamingChatLanguageModel = streamingChatLanguageModel;
        this.toolSpecifications = toolSpecifications;
    }

    @GetMapping("/v1/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatLanguageModel.generate(message));
    }

    @GetMapping("/v1/prompt")
    public Response<AiMessage> prompt( Map<String, Object> variables) {
        PromptTemplate promptTemplate = PromptTemplate.from("Tell me a {adjective} joke about {topic}");
        Prompt prompt = promptTemplate.apply(variables);
        return chatLanguageModel.generate(List.of(new UserMessage(prompt.text())), toolSpecifications);
    }

    @PostMapping("/v1/chat/completions")
    public Flux<AiMessage> chatCompletions(@RequestBody ApiRequest.ChatCompletionRequest chatRequest) {

        chatRequest.messages().forEach(System.out::println);

        List<ChatMessage> messages = chatRequest.messages().stream().map(msg -> {
            switch (msg.role()) {
                case ASSISTANT:
                    return new AiMessage(msg.content());
                case SYSTEM:
                    return new SystemMessage(msg.content());
                default:
                    return new UserMessage(msg.content());
            }
        }).collect(Collectors.toList());
        //
        return Flux.create(sink -> {
            streamingChatLanguageModel.generate(messages, new StreamingResponseHandler<>() {

                @Override
                public void onNext(String token) {
                    System.out.println("Token: " + token);
                    sink.next(new AiMessage(token));
                }

                @Override
                public void onComplete(Response<AiMessage> response) {
                    sink.complete();
                }

                @Override
                public void onError(Throwable error) {
                    System.out.println("Error: " + error.getMessage());
                    sink.error(error);
                }

            });
        });

    }





}
