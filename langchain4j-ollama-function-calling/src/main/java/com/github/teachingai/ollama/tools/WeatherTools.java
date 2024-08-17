package com.github.teachingai.ollama.tools;

public class WeatherTools {
    @Tool("Returns the weather forecast for a given city")
    String getWeather(
            @P("The city for which the weather forecast should be returned") String city,
            TemperatureUnit temperatureUnit
    ) {
        return "The weather in " + city + " is 25 degrees Celsius";
    }


}
