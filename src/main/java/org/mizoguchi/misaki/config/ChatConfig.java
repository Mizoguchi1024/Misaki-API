package org.mizoguchi.misaki.config;

import org.mizoguchi.misaki.advisor.TreeMemoryAdvisor;
import org.mizoguchi.misaki.mapper.MessageMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {
    @Bean
    public ChatClient chatClient(DeepSeekChatModel model, MessageMapper messageMapper) {
        return ChatClient.builder(model)
                .defaultAdvisors(TreeMemoryAdvisor.builder(messageMapper).build())
                .build();
    }
}
