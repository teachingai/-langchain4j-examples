package com.github.teachingai.ollama;

import com.github.teachingai.ollama.tools.GetWeatherFunction;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;

import static dev.langchain4j.agent.tool.JsonSchemaProperty.*;
import static dev.langchain4j.internal.Json.fromJson;

public class Langchain4jOllamaFunctionCallingTest1 {

    interface Assistant {
        String chat(String userMessage);
    }


    public static void main(String[] args) {

        ChatLanguageModel chatLanguageModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("qwen:7b") // try "mistral", "llama2", "codellama", "phi" or "tinyllama"
                .temperature(0.9D)
                .timeout(Duration.ofSeconds(60))
                .build();

        ToolSpecification toolSpecification = ToolSpecification.builder()
                .name("getWeather")
                .description("Returns the weather forecast for a given city")
                .addParameter("city", type("string"), description("The city for which the weather forecast should be returned"))
                .addParameter("temperatureUnit", enums(GetWeatherFunction.TemperatureUnit.class)) // enum TemperatureUnit { CELSIUS, FAHRENHEIT }
                .build();


        ToolExecutor toolExecutor = (toolExecutionRequest, memoryId) -> {
            Map<String, Object> arguments = fromJson(toolExecutionRequest.arguments());
            String bookingNumber = arguments.get("bookingNumber").toString();
            Booking booking = getBooking(bookingNumber);
            return booking.toString();
        };

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(Collections.singletonMap(toolSpecification, toolExecutor))
                .build();

        /**
         * qwen2:7b ：https://ollama.com/library/qwen2
         * mistral ：https://ollama.com/library/mistral
         */
        var ollamaApi = new MyOllamaApi();
        var chatClient = new MyOllamaChatClient(ollamaApi, OllamaChatOptions.builder()
                .withModel("qwen:7b")
                .withFormat("json")
                .withFunction("CurrentWeather")
                .withTemperature(0.9f)
                .build());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(">>> ");
            String message = scanner.nextLine();
            if (message.equals("exit")) {
                break;
            }

            String resp = chatClient.call(message);
            System.out.println("<<< " + resp);
        }
    }

}
