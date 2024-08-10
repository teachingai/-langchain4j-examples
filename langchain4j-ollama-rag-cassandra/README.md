## langchain4j-ollama-rag-cassandra

> 基于 [Spring Boot 3.x](https://docs.spring.io/spring-boot/index.html) 、[LangChain4j](https://github.com/langchain4j)、[Ollama](https://ollama.com/) 和 [Cassandra](https://cassandra.apache.org/) 的 检索增强生成（Retrieval-augmented Generation）功能示例。

### 先决条件

- 您首先需要在本地计算机上运行 Ollama。请参阅官方 [Ollama 项目自述文件](https://github.com/ollama/ollama "Ollama 项目自述文件")，开始在本地计算机上运行模型。
- 其次，您需要安装并运行 Cassandra 数据库。请参阅 [Cassandra 官方网站](https://cassandra.apache.org/ "Cassandra 官方网站")，了解如何安装和运行 Cassandra。

#### 添加存储库和 BOM

LangChain4j 工件发布在 `Maven Central` 存储库中。请参阅 [Maven Repositories](https://mvnrepository.com/artifact/dev.langchain4j) 将这些存储库添加到您的构建系统中。

为了帮助进行依赖管理，LangChain4j 提供了 [BOM（物料清单）](https://mvnrepository.com/artifact/dev.langchain4j/langchain4j-bom)，以确保在整个项目中使用一致的 LangChain4j 版本。请将 LangChain4j BOM 添加到您的构建系统。

#### 自动配置

##### LangChain4j

LangChain4j 为 Ollama 聊天客户端提供 Spring Boot 自动配置。要启用它，请将以下依赖项添加到项目的 Maven `pom.xml` 文件中：

```xml
<dependency>
  <groupId>dev.langchain4j</groupId>
  <artifactId>langchain4j-ollama-spring-boot-starter</artifactId>
</dependency>
```

或者，在你的 Gradle 构建文件 `build.gradle` 中添加：

```groovy
dependencies {
  implementation 'dev.langchain4j:langchain4j-ollama-spring-boot-starter'
}
```

###### Cassandra For Testcontainers

Testcontainers 为 Cassandra 提供了 Java 依赖。要启用它，请将以下依赖项添加到项目的 Maven `pom.xml` 文件中：

```xml
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>cassandra</artifactId>
  <scope>test</scope>
</dependency>
```

或者，在你的 Gradle 构建文件 `build.gradle` 中添加：

```groovy
dependencies {
    implementation 'org.testcontainers:cassandra'
}
```

示例代码：

```java
var cassandra = new CassandraContainer<>(DockerImageName.parse("cassandra:3.11.2"));
cassandra.start();
```

### LangChain4j Embedding 模型

LangChain4j 提供了一些 [Embedding 模型](https://github.com/langchain4j/langchain4j-embeddings)，可用于 文本嵌入（Embeddings）的学习。

#### 模型概览

- langchain4j-embeddings-all-minilm-l6-v2-q
- langchain4j-embeddings-all-minilm-l6-v2
- langchain4j-embeddings-bge-small-en-q
- langchain4j-embeddings-bge-small-en-v15-q
- langchain4j-embeddings-bge-small-en-v15
- langchain4j-embeddings-bge-small-en
- langchain4j-embeddings-bge-small-zh-q
- langchain4j-embeddings-all-minilm-l6-v2-q
- langchain4j-embeddings-all-minilm-l6-v2
- langchain4j-embeddings-bge-small-zh
- langchain4j-embeddings-e5-small-v2-q
- langchain4j-embeddings-e5-small-v2

##### Git LFS

- 这里要注意的是，由于`.onnx`模型文件较大，[langchain4j-embeddings](https://github.com/langchain4j/langchain4j-embeddings) 与 [langchain4j](https://github.com/langchain4j/langchain4j) 是分开的。
- 并且 `.onnx` 文件存储在 Git LFS 中。
- 为了能够在本地运行集成测试，您需要从 LFS 下载`.onnx`文件。
- 请安装 Git LFS 并执行 `git lfs pull`。


### Ollama Embedding 模型

> [danger] 以下我们选择了几个专业的 Embedding 的模型，用于进行 文本嵌入（Embeddings）的学习。

#### 模型概览

- **shaw/dmeta-embedding-zh**：小型中文 Embedding 模型，适合多种场景，特别是在语义检索和 RAG 应用中表现出色。参数大小仅 400MB，支持上下文窗口长度达到 1024，推理成本较低。
- **mxbai-embed-large**：截至 2024 年 3 月，在 MTEB 上创下了 Bert-large 尺寸模型的 SOTA 性能记录，具有很好的泛化能力。
- **nomic-embed-text**：大上下文长度文本编码器，超越了 OpenAI `text-embedding-ada-002`，在短上下文和长上下文任务上表现优异。
- **snowflake-arctic-embed**：专注于性能优化的高质量检索模型，提供不同参数大小的版本以适应不同的性能和资源需求。
- **snowflake-arctic-embed**：专注于性能优化的高质量检索模型，提供不同参数大小的版本以适应不同的性能和资源需求。

**注意事项**

- Windows 用户启动 Ollama 后，系统托盘会出现图标，表明服务已启动。
- 访问 Embedding 服务时无需运行 `ollama run`，仅在需要 chat 功能时才需启动大模型。

#### shaw/dmeta-embedding-zh

> Dmeta-embedding 是一款跨领域、跨任务、开箱即用的中文 Embedding 模型，适用于搜索、问答、智能客服、LLM+RAG 等各种业务场景，支持使用 Transformers/Sentence-Transformers/Langchain 等工具加载推理。

- Huggingface：https://huggingface.co/DMetaSoul/Dmeta-embedding-zh
- 文档地址：https://ollama.com/shaw/dmeta-embedding-zh

**优势特点如下**：

- 多任务、场景泛化性能优异，目前已取得 MTEB 中文榜单第二成绩（2024.01.25）
- 模型参数大小仅 400MB，对比参数量超过 GB 级模型，可以极大降低推理成本
- 支持上下文窗口长度达到 1024，对于长文本检索、RAG 等场景更适配

**该模型有 4 个不通的版本**：

- [dmeta-embedding-zh](https://ollama.com/shaw/dmeta-embedding-zh)：`shaw/dmeta-embedding-zh` 是一个参数量只有400M、适用于多种场景的中文Embedding模型，在MTEB基准上取得了优异成绩，尤其适合语义检索、RAG等LLM应用。
- [dmeta-embedding-zh-q4](https://ollama.com/shaw/dmeta-embedding-zh-q4)：`shaw/dmeta-embedding-zh` 的 Q4_K_M 量化版本
- [dmeta-embedding-zh-small](https://ollama.com/shaw/dmeta-embedding-zh-small)：`shaw/dmeta-embedding-zh-small` 是比 `shaw/dmeta-embedding-zh` 更轻量化的模型，参数不足300M，推理速度提升30%。
- [dmeta-embedding-zh-small-q4](https://ollama.com/shaw/dmeta-embedding-zh-small-q4)：`shaw/dmeta-embedding-zh-small` 的 Q4_K_M 量化版本

```shell
ollama pull shaw/dmeta-embedding-zh
```

#### mxbai-embed-large

> 截至 2024 年 3 月，该模型在 MTEB 上创下了 Bert-large 尺寸模型的 SOTA 性能记录。它的表现优于 OpenAIs `text-embedding-3-large` 模型等商业模型，并且与其尺寸 20 倍的模型的性能相当。
`mxbai-embed-large`在没有 MTEB 数据重叠的情况下进行训练，这表明该模型在多个领域、任务和文本长度方面具有很好的泛化能力。

文档地址：https://ollama.com/library/mxbai-embed-large

```shell
ollama pull mxbai-embed-large
```

#### nomic-embed-text

> nomic-embed-text 是一个大上下文长度文本编码器，超越了 OpenAI `text-embedding-ada-002`，并且`text-embedding-3-small`在短上下文和长上下文任务上表现优异。

文档地址：https://ollama.com/library/nomic-embed-text

```shell
ollama pull nomic-embed-text
```

#### snowflake-arctic-embed

> snowflake-arctic-embed 是一套文本嵌入模型，专注于创建针对性能优化的高质量检索模型。

这些模型利用现有的开源文本表示模型（例如 bert-base-uncased）进行训练，并在多阶段管道中进行训练以优化其检索性能。

**该模型有 5 种参数大小**：

- snowflake-arctic-embed:335m（默认）
- snowflake-arctic-embed:137m
- snowflake-arctic-embed:110m
- snowflake-arctic-embed:33m
- snowflake-arctic-embed:22m

文档地址：https://ollama.com/library/snowflake-arctic-embed

```shell
ollama pull snowflake-arctic-embed
```

#### all-minilm

> all-minilm 是一款在非常大的句子级数据集上训练的嵌入模型。

文档地址：https://ollama.com/library/all-minilm

```shell
ollama pull all-minilm
```

### Cassandra 向量数据库

> 本示例使用 Cassandra 向量数据库存储 Embedding 向量。




### RAG 示例




