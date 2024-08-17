package com.github.hiwepy.vertexai.functions;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static dev.langchain4j.agent.tool.JsonSchemaProperty.*;
@Configuration
public class FunctionConfig {

    @Bean
    public List<ToolSpecification> toolSpecifications() {
        List<ToolSpecification> toolSpecifications = List.of();

        toolSpecifications.add(ToolSpecification.builder()
                .name("getWeather")
                .description("Returns the weather forecast for a given city")
                .addParameter("city", type("string"), description("The city for which the weather forecast should be returned"))
                .addParameter("unit", enums(GetWeatherFunction.TemperatureUnit.class)) // enum TemperatureUnit { CELSIUS, FAHRENHEIT }
                .build());

        toolSpecifications.addAll(ToolSpecifications.toolSpecificationsFrom(new GetWeatherFunction()));
        toolSpecifications.addAll(ToolSpecifications.toolSpecificationsFrom(new PconlineRegionFunction()));

        return toolSpecifications;
    }

}
