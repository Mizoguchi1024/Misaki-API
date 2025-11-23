package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Conversation;
import org.mizoguchi.misaki.entity.Message;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    Long createConversation(Long userId);
    Conversation getConversation(Long userId, Long conversationId);
    List<Conversation> getConversations(Long userId);
    Flux<String> sendMessage(Long userId, Long assistantId, Long conversationId, String content, String prefix);
    List<Message> getMessages(Long userId, Long conversationId);
    String getTitle(Long userId, Long conversationId);
}
