package com.github.teachingai.ollama;


import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.Scanner;

public class OllamaEmbeddingTest {

    public static void main(String[] args) {

        // 指定使用的模型
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("shaw/dmeta-embedding-zh")
                .build();
        // 测试数据
        RedisEmbeddingStore vectorStore = RedisEmbeddingStore.builder()
                .host("192.168.1.170")
                .port(16379)
                .password("hpkRzkC7pcDnYp3e")
                .dimension(384)
                .build();

        vectorStore.add(embeddingModel.embed("白日依山尽，黄河入海流。欲穷千里目，更上一层楼。").content(), TextSegment.from("白日依山尽，黄河入海流。欲穷千里目，更上一层楼。"));
        vectorStore.add(embeddingModel.embed("青山依旧在，几度夕阳红。白发渔樵江渚上，惯看秋月春风。").content(), TextSegment.from("青山依旧在，几度夕阳红。白发渔樵江渚上，惯看秋月春风。"));
        vectorStore.add(embeddingModel.embed("一片孤城万仞山，羌笛何须怨杨柳。春风不度玉门关。").content(), TextSegment.from("一片孤城万仞山，羌笛何须怨杨柳。春风不度玉门关。"));
        vectorStore.add(embeddingModel.embed("危楼高百尺，手可摘星辰。不敢高声语，恐惊天上人。").content(), TextSegment.from("危楼高百尺，手可摘星辰。不敢高声语，恐惊天上人。"));

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("请输入关键词: ");
            String message = scanner.nextLine();
            if (message.equals("exit")) {
                break;
            }
            EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                    .queryEmbedding(embeddingModel.embed(message).content())
                    .maxResults(3)
                    .minScore(0.5)
                    //.filter(Filter.)
                    .build();
            EmbeddingSearchResult<TextSegment> embeddingSearchResult = vectorStore.search(request);
            System.out.println("查询结果: ");
            for (EmbeddingMatch<TextSegment> doc : embeddingSearchResult.matches()) {
                System.out.println(doc.embeddingId() + ":" + doc.embedding());
            }
        }
    }

}
