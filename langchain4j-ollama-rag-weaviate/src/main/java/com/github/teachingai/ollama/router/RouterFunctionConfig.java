package com.github.teachingai.ollama.router;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class RouterFunctionConfig {

    @Bean
    RouterFunction<ServerResponse> routes(ChatLanguageModel chatLanguageModel, StreamingChatLanguageModel streamingChatLanguageModel) {
        return RouterFunctions.route()
                .GET("/generate", req ->
                        ServerResponse.ok().body(
                                chatLanguageModel.generate(req.param("message")
                                        .orElse("tell me a joke"))))
                .GET("/prompt", req -> {
                    PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
                    Prompt prompt = new Prompt(req.param("prompt").orElse("Tell me a joke"));
                    return ServerResponse.ok().body( chatLanguageModel.generate(prompt.toUserMessage()));
                })
                .GET("/chat", req -> {
                    Prompt prompt = new Prompt(req.param("prompt").orElse("Tell me a joke"));
                    return ServerResponse.ok().body(streamingChatLanguageModel.generate(prompt.toUserMessage(), new StreamingResponseHandler<>() {

                        @Override
                        public void onNext(String token) {
                            System.out.println("Token: " + token);
                            //sink.next(new AiMessage(token));
                        }

                        @Override
                        public void onComplete(Response<AiMessage> response) {
                            //sink.complete();
                        }

                        @Override
                        public void onError(Throwable error) {
                            System.out.println("Error: " + error.getMessage());
                           // sink.error(error);
                        }

                    }));
                })
                .build();
    }

}
