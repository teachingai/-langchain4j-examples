package com.github.teachingai.ollama.tools;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.benmanes.caffeine.cache.*;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
/**
 * 免费天气查询
 * https://www.sojson.com/api/weather.html
 */
@Slf4j
public class GetWeatherFunction {

    //请求连接地址
    private final static String SOJSON_WEATHER_URL = "http://t.weather.sojson.com/api/weather/city";

    private final static RestClient restClient = RestClient.builder().baseUrl(SOJSON_WEATHER_URL).build();

    private final LoadingCache<String, Optional<JSONObject>> WEATHER_DATA_CACHES = Caffeine.newBuilder()
            // 设置写缓存后1个小时过期
            .expireAfterWrite(1, TimeUnit.HOURS)
            // 设置缓存容器的初始容量为10
            .initialCapacity(10)
            // 设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
            .maximumSize(100)
            // 设置要统计缓存的命中率
            .recordStats()
            // 设置缓存的移除通知
            .removalListener(new RemovalListener<String, Optional<JSONObject>>() {

                @Override
                public void onRemoval(@Nullable String key, @Nullable Optional<JSONObject> value, @NonNull RemovalCause cause) {
                    log.info("{} was removed, cause is {}", key, cause);
                }

            })
            // build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
            .build(new CacheLoader<String, Optional<JSONObject>>() {

                @Override
                public Optional<JSONObject> load(String city_code) throws Exception {
                    HttpEntity<String> response = restClient.get().uri(String.format("/api/weather/city/%s", city_code))
                            .retrieve().toEntity(String.class);
                    String bodyString = response.getBody();
                    log.info("city_code {} >> weather :  {}", city_code, bodyString);
                    JSONObject jsonObject = JSONObject.parseObject(bodyString);
                    return Optional.ofNullable(jsonObject);
                }
            });


    public enum TemperatureUnit { C, F }

    @JsonClassDescription("Get the weather in location")
    public record Request( @JsonProperty("cityId") @P("城市ID") String cityId,
                           @JsonProperty("unit") @P("温度单位，C:摄氏度，F: 华氏度") TemperatureUnit unit) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Response(
            @JsonProperty("shidu") double shidu,
            @JsonProperty("pm25") double pm25,
            @JsonProperty("pm10") double pm10,
            @JsonProperty("quality") String quality,
            @JsonProperty("wendu") double wendu,
            @JsonProperty("unit") TemperatureUnit unit) {
        public Response(double wendu, TemperatureUnit unit) {
            this(0.0, 0.0, 0.0, "", wendu, unit);
        }

    }

    @Tool("获取天气：根据给出的城市ID获取天气信息")
    public Response getWeather(@P("获取天气的请求参数对象") Request request) {
        Optional<JSONObject> opt = WEATHER_DATA_CACHES.get(request.cityId());
        if (opt.isPresent()) {
            JSONObject jsonObject = opt.get();
            JSONObject data = jsonObject.getJSONObject("data");
            return new Response(data.getDouble("wendu"), data.getDouble("pm25"), data.getDouble("pm10"),
                    data.getString("quality"), data.getDouble("wendu"), TemperatureUnit.C);
        }
        return new Response(30.0, TemperatureUnit.C);
    }

}
