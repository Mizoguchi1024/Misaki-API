package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Conversation;
import org.mizoguchi.misaki.entity.Message;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    Long createConversation(String email);
    Conversation getConversation(String email, Long conversationId);
    List<Conversation> getConversations(String email);
    Flux<String> sendMessage(String email, Long conversationId, String content, String prefix);
    List<Message> getMessages(String email, Long conversationId);
    String getTitle(String email, Long conversationId);
}
