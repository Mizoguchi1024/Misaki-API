package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Conversation;
import org.mizoguchi.misaki.entity.Message;
import org.mizoguchi.misaki.entity.vo.UserConversationResponse;
import org.mizoguchi.misaki.entity.vo.UserMessageResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    Long createConversation(Long userId);
    Conversation getConversationEntity(Long userId, Long conversationId);
    List<UserConversationResponse> getConversations(Long userId);
    Flux<String> sendMessage(Long userId, Long conversationId, String content, String prefix);
    List<Message> getMessagesEntity(Long userId, Long conversationId);
    List<UserMessageResponse> getMessages(Long userId, Long conversationId);
    String getTitle(Long userId, Long conversationId);
}
