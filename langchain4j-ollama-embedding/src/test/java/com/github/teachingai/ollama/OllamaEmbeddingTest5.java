package com.github.teachingai.ollama;

import com.alibaba.fastjson2.JSONObject;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;
import java.util.Scanner;

/**
 * 该示例用于学习文档解析、嵌入、简单的相似度搜索
 */
public class OllamaEmbeddingTest5 {

    /**
     * 下面代码依赖 langchain4j-pdf-document-reader 和 pdfbox（3.0.2）
     */
    public static void main(String[] args) {

        /**
         * mxbai-embed-large ：https://ollama.com/library/mxbai-embed-large
         * nomic-embed-text ：https://ollama.com/library/nomic-embed-text
         * snowflake-arctic-embed ：https://ollama.com/library/snowflake-arctic-embed
         * shaw/dmeta-embedding-zh：https://ollama.com/shaw/dmeta-embedding-zh
         */
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("shaw/dmeta-embedding-zh")
                .build();
        /**
         * 1、解析 llama2.pdf
         */
        ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader("classpath:/llama2.pdf",
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());
        /**
         * 2、读取并处理PDF文档以提取段落。
         */
        List<Document> documents = pdfReader.get();
        for (Document document : documents) {
            System.out.println( JSONObject.of( "id", document.getId(), "embedding", embeddingClient.embed(document),"content", document.getContent(), "metadata", document.getMetadata()));
        }

        /**
         * 3、简单的相似度搜索
         */
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.add(documents);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("请输入关键词: ");
            String query = scanner.nextLine();
            if (query.equals("exit")) {
                break;
            }
            System.out.print("Embedding Query: " + embeddingClient.embed(query));
            // Retrieve embeddings
            SearchRequest request = SearchRequest.query(query).withTopK(2).withSimilarityThreshold(0.5);
            List<Document> similarDocuments  = embeddingStore.similaritySearch(request);
            System.out.println("查询结果: ");
            for (Document document : similarDocuments ) {
                System.out.println( JSONObject.of( "id", document.getId(), "embedding", document.getEmbedding(),"content", document.getContent(), "metadata", document.getMetadata()));
            }
        }
    }

}
