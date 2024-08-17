package com.github.teachingai.ollama;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.PoolingMode;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.io.IOException;

public class InProcessEmbeddingModelExamples {

    static class Pre_Packaged_In_Process_Embedding_Model_Example {

        public static void main(String[] args) throws IOException {

            String text = "Let's demonstrate that embedding can be done within a Java process and entirely offline.";

            // requires "langchain4j-embeddings-all-minilm-l6-v2" Maven/Gradle dependency, see pom.xml
            EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

            Embedding inProcessEmbedding = embeddingModel.embed(text).content();
            System.out.println(inProcessEmbedding);

            // Uncomment to compare with embedding generated by HuggingFace
            // EmbeddingModel huggingFaceEmbeddingModel = HuggingFaceEmbeddingModel.builder()
            //        .accessToken(System.getenv("HF_API_KEY"))
            //        .modelId("sentence-transformers/all-MiniLM-L6-v2")
            //        .build();

            //Embedding huggingFaceEmbedding = huggingFaceEmbeddingModel.embed(text).content();

            //System.out.println(CosineSimilarity.between(inProcessEmbedding, huggingFaceEmbedding));
            // 1.000000001963221 <- this indicates that the embedding created by the offline in-process all-MiniLM-L6-v2 model
            // is practically identical to that generated using the HuggingFace API.
        }
    }

    static class Custom_In_Process_Embedding_Model_Example {

        public static void main(String[] args) throws IOException {

            // You can use many of the embedding models from Hugging Face.
            // https://huggingface.co/Xenova repository has a lot of popular models converted into ONNX format.

            // For example, https://huggingface.co/Xenova/multilingual-e5-large
            // Go to "Files and versions": https://huggingface.co/Xenova/multilingual-e5-large/tree/main
            // Download "tokenizer.json": https://huggingface.co/Xenova/multilingual-e5-large/resolve/main/tokenizer.json?download=true
            // Go to "onnx" directory: https://huggingface.co/Xenova/multilingual-e5-large/tree/main/onnx
            // Download "model_quantized.onnx": https://huggingface.co/Xenova/multilingual-e5-large/resolve/main/onnx/model_quantized.onnx?download=true
            // Go to the original model repo: https://huggingface.co/intfloat/multilingual-e5-large
            // Go to "Files and versions": https://huggingface.co/intfloat/multilingual-e5-large/tree/main
            // Go to "1_Pooling": https://huggingface.co/intfloat/multilingual-e5-large/tree/main/1_Pooling
            // Go to "config.json": https://huggingface.co/intfloat/multilingual-e5-large/blob/main/1_Pooling/config.json
            // Note "pooling_mode_mean_tokens": true, this means that we need to use PoolingMode.MEAN

            // You can also convert any other model into ONNX format by following this guide: https://huggingface.co/docs/optimum/exporters/onnx/usage_guides/export_a_model

            // requires "langchain4j-embeddings" Maven/Gradle dependency, see pom.xml
            EmbeddingModel embeddingModel = new OnnxEmbeddingModel(
                    "/home/langchain4j/model_quantized.onnx",
                    "/home/langchain4j/tokenizer.json",
                    PoolingMode.MEAN
            );

            String englishText = "Hello, how are you doing?";
            String frenchText = "Bonjour comment allez-vous?";

            Embedding englishTextEmbedding = embeddingModel.embed(englishText).content();
            Embedding frenchTextEmbedding = embeddingModel.embed(frenchText).content();

            System.out.println(CosineSimilarity.between(englishTextEmbedding, frenchTextEmbedding)); // 0.9060777281158113
        }
    }
}