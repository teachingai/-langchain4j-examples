package com.github.teachingai.ollama;

import com.alibaba.fastjson2.JSONObject;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.weaviate.WeaviateEmbeddingStore;

import java.util.Scanner;

public class OllamaEmbeddingTest {

    private static String weaviateHost = "http://localhost:11434";
    private static String collectionName = "langchain4j-" + Utils.randomUUID();

    public static void main(String[] args) {

        /**
         * mxbai-embed-large ：https://ollama.com/library/mxbai-embed-large
         * nomic-embed-text ：https://ollama.com/library/nomic-embed-text
         * snowflake-arctic-embed ：https://ollama.com/library/snowflake-arctic-embed
         * shaw/dmeta-embedding-zh：https://ollama.com/shaw/dmeta-embedding-zh
         */
        // 指定使用的模型
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("shaw/dmeta-embedding-zh")
                .build();
        // 测试数据
        EmbeddingStore<TextSegment> embeddingStore = embeddingStore = WeaviateEmbeddingStore.builder()
                .scheme("http")
                .host(weaviateHost)
                // "Default" class is used if not specified. Must start from an uppercase letter!
                .objectClass("Test")
                // If true (default), then WeaviateEmbeddingStore will generate a hashed ID based on provided
                // text segment, which avoids duplicated entries in DB. If false, then random ID will be generated.
                .avoidDups(true)
                // Consistency level: ONE, QUORUM (default) or ALL.
                .consistencyLevel("ALL")
                .build();

        // 将嵌入存储在 VectorStore
        embeddingStore.add(embeddingModel.embed("白日依山尽，黄河入海流。欲穷千里目，更上一层楼。").content(), TextSegment.from("白日依山尽，黄河入海流。欲穷千里目，更上一层楼。"));
        embeddingStore.add(embeddingModel.embed("青山依旧在，几度夕阳红。白发渔樵江渚上，惯看秋月春风。").content(), TextSegment.from("青山依旧在，几度夕阳红。白发渔樵江渚上，惯看秋月春风。"));
        embeddingStore.add(embeddingModel.embed("一片孤城万仞山，羌笛何须怨杨柳。春风不度玉门关。").content(), TextSegment.from("一片孤城万仞山，羌笛何须怨杨柳。春风不度玉门关。"));
        embeddingStore.add(embeddingModel.embed("危楼高百尺，手可摘星辰。不敢高声语，恐惊天上人。").content(), TextSegment.from("危楼高百尺，手可摘星辰。不敢高声语，恐惊天上人。"));
        /**
         * 2、简单的相似度搜索
         */
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("请输入关键词: ");
            String query = scanner.nextLine();
            if (query.equals("exit")) {
                break;
            }
            Response<Embedding> queryEmbedding = embeddingModel.embed(query);
            System.out.println("Embedding Query: " + queryEmbedding.content());
            EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding.content())
                    .maxResults(5)
                    .minScore(0.1)
                    //.filter(new IsGreaterThanOrEqualTo(key, value))
                    .build();
            EmbeddingSearchResult<TextSegment> embeddingSearchResult = embeddingStore.search(request);
            System.out.println("查询结果: ");
            for (EmbeddingMatch<TextSegment> doc : embeddingSearchResult.matches()) {
                System.out.println( JSONObject.of( "id", doc.embeddingId(), "score", doc.score(),"content", doc.embedded().text(),"embedding", doc.embedding().vector()));
            }
        }
    }

}
