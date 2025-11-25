package com.gergov.trainingPlan_svc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;

@Configuration
public class AiConfig {

    private static final Logger log = LoggerFactory.getLogger(AiConfig.class);

    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        if (chatModel == null) {
            log.error("OpenAiChatModel bean is null - check spring.ai.openai.model property");
            throw new IllegalStateException("OpenAiChatModel bean is not configured");
        }
        return ChatClient.builder(chatModel).build();
    }
}
