package com.github.teachingai.ollama;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.weaviate.WeaviateEmbeddingStore;
import org.testcontainers.weaviate.WeaviateContainer;

import java.util.List;
import java.util.Scanner;

public class Langchain4jOllamaRagTest {

    public static void main(String[] args) {

        try (WeaviateContainer weaviate = new WeaviateContainer("semitechnologies/weaviate:1.22.4")) {
            weaviate.start();

            EmbeddingStore<TextSegment> embeddingStore = WeaviateEmbeddingStore.builder()
                    .scheme("http")
                    .host(weaviate.getHttpHostAddress())
                    // "Default" class is used if not specified. Must start from an uppercase letter!
                    .objectClass("Test")
                    // If true (default), then WeaviateEmbeddingStore will generate a hashed ID based on provided
                    // text segment, which avoids duplicated entries in DB. If false, then random ID will be generated.
                    .avoidDups(true)
                    // Consistency level: ONE, QUORUM (default) or ALL.
                    .consistencyLevel("ALL")
                    .build();

            EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

            TextSegment segment1 = TextSegment.from("I like football.");
            Embedding embedding1 = embeddingModel.embed(segment1).content();
            embeddingStore.add(embedding1, segment1);

            TextSegment segment2 = TextSegment.from("The weather is good today.");
            Embedding embedding2 = embeddingModel.embed(segment2).content();
            embeddingStore.add(embedding2, segment2);

            Embedding queryEmbedding = embeddingModel.embed("What is your favourite sport?").content();
            List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);
            EmbeddingMatch<TextSegment> embeddingMatch = relevant.get(0);

            System.out.println(embeddingMatch.score()); // 0.8144288063049316
            System.out.println(embeddingMatch.embedded().text()); // I like football.
        }
    }

        var ollamaApi = new OllamaApi();
        //指定使用的模型
        var embeddingClient = new OllamaEmbeddingClient(ollamaApi)
                .withDefaultOptions(OllamaOptions.create().withModel("gemma"));
        //测试数据
        VectorStore vectorStore = new SimpleVectorStore(embeddingClient);
        vectorStore.add(List.of(
                new Document("白日依山尽，黄河入海流。欲穷千里目，更上一层楼。"),
                new Document("青山依旧在，几度夕阳红。白发渔樵江渚上，惯看秋月春风。"),
                new Document("一片孤城万仞山，羌笛何须怨杨柳。春风不度玉门关。"),
                new Document("危楼高百尺，手可摘星辰。不敢高声语，恐惊天上人。")
        ));
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("请输入关键词: ");
            String message = scanner.nextLine();
            if (message.equals("exit")) {
                break;
            }
            List<Document> documents = vectorStore.similaritySearch(message);
            System.out.println("查询结果: ");
            for (Document doc : documents) {
                System.out.println(doc.getContent());
            }
        }
    }

}
