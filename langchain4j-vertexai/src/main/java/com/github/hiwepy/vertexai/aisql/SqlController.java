package com.github.hiwepy.vertexai.aisql;

import com.github.hiwepy.vertexai.exception.SqlGenerationException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@RestController
public class SqlController {

    @Value("classpath:/schema.sql")
    private Resource ddlResource;

    private final PromptTemplate sqlPromptTemplate;

    private final ChatLanguageModel chatLanguageModel;
    private final JdbcTemplate jdbcTemplate;

    public SqlController(
            @Value("classpath:/sql-prompt-template.st") Resource sqlPromptTemplateResource,
            ChatLanguageModel chatLanguageModel,
            JdbcTemplate jdbcTemplate) throws IOException {
        this.chatLanguageModel = chatLanguageModel;
        this.jdbcTemplate = jdbcTemplate;
        this.sqlPromptTemplate = new PromptTemplate(sqlPromptTemplateResource.getContentAsString(Charset.defaultCharset()));
    }

    @PostMapping(path="/sql")
    public Answer sql(@RequestBody SqlRequest sqlRequest) throws IOException {
        String schema = ddlResource.getContentAsString(Charset.defaultCharset());
        Prompt prompt = sqlPromptTemplate.apply(Map.of("question", sqlRequest.question(), "ddl", schema));
        String query = chatLanguageModel.generate(prompt.text());

        if (query.toLowerCase().startsWith("select")) {
            return new Answer(query, jdbcTemplate.queryForList(query));
        }

        throw new SqlGenerationException(query);
    }

    public record SqlRequest(String question) {}

}
