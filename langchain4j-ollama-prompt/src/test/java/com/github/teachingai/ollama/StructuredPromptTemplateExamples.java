package com.github.teachingai.ollama;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.time.Duration;
import java.util.List;

import static java.util.Arrays.asList;

public class StructuredPromptTemplateExamples {

    static ChatLanguageModel model = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("qwen:7b") // try "mistral", "llama2", "codellama", "phi" or "tinyllama"
            .temperature(0.9D)
            .timeout(Duration.ofSeconds(60))
            .build();

    static class Simple_Structured_Prompt_Example {

        @StructuredPrompt("Create a recipe of a {{dish}} that can be prepared using only {{ingredients}}")
        static class CreateRecipePrompt {

            private String dish;
            private List<String> ingredients;
        }

        public static void main(String[] args) {

            CreateRecipePrompt createRecipePrompt = new CreateRecipePrompt();
            createRecipePrompt.dish = "salad";
            createRecipePrompt.ingredients = asList("cucumber", "tomato", "feta", "onion", "olives");
            Prompt prompt = StructuredPromptProcessor.toPrompt(createRecipePrompt);

            AiMessage aiMessage = model.generate(prompt.toUserMessage()).content();
            System.out.println(aiMessage.text());
        }
    }

    static class Multi_Line_Structured_Prompt_Example {

        @StructuredPrompt({
                "Create a recipe of a {{dish}} that can be prepared using only {{ingredients}}.",
                "Structure your answer in the following way:",

                "Recipe name: ...",
                "Description: ...",
                "Preparation time: ...",

                "Required ingredients:",
                "- ...",
                "- ...",

                "Instructions:",
                "- ...",
                "- ..."
        })
        static class CreateRecipePrompt {

            private String dish;
            private List<String> ingredients;
        }

        public static void main(String[] args) {

            CreateRecipePrompt createRecipePrompt = new CreateRecipePrompt();
            createRecipePrompt.dish = "salad";
            createRecipePrompt.ingredients = asList("cucumber", "tomato", "feta", "onion", "olives");
            Prompt prompt = StructuredPromptProcessor.toPrompt(createRecipePrompt);

            AiMessage aiMessage = model.generate(prompt.toUserMessage()).content();
            System.out.println(aiMessage.text());
        }
    }
}