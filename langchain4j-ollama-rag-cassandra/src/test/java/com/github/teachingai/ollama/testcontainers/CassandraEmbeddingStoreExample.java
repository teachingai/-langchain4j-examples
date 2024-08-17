package com.github.teachingai.ollama.testcontainers;

import java.util.List;

public class CassandraEmbeddingStoreExample {

    public static void main(String[] args) {

        try (CassandraContainer cassandra = new CassandraContainer<>(DockerImageName.parse("cassandra:3.11.2"))) {
            cassandra.start();

            EmbeddingStore<TextSegment> embeddingStore = CassandraEmbeddingStore
                    .builder()
                    .baseUrl(cassandra.getEndpoint())
                    .collectionName(randomUUID())
                    .logRequests(true)
                    .logResponses(true)
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

            System.out.println(embeddingMatch.score()); // 0.8144288493114709
            System.out.println(embeddingMatch.embedded().text()); // I like football.

            embeddingStore.removeAll();
        }
    }

}
