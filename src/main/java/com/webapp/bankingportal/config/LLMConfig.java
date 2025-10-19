package com.webapp.bankingportal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.model.openai.OpenAiChatModel;

@Configuration
public class LLMConfig {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.model}")
    private String modelName;

    @Value("${groq.api.endpoint}")
    private String baseUrl;

    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .build();
    }
}
