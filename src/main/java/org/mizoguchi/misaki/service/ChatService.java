package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Conversation;
import org.mizoguchi.misaki.entity.Message;
import org.mizoguchi.misaki.entity.vo.front.ConversationFrontResponse;
import org.mizoguchi.misaki.entity.vo.front.MessageFrontResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    Long addConversation(Long userId);
    Conversation getConversationEntity(Long userId, Long conversationId);
    List<ConversationFrontResponse> listConversationsFrontResponse(Long userId);
    String getConversationTitle(Long userId, Long conversationId);

    Flux<String> sendMessage(Long userId, Long conversationId, String content, String prefix);
    List<Message> listMessagesEntity(Long userId, Long conversationId);
    List<MessageFrontResponse> listMessagesFrontResponse(Long userId, Long conversationId);
}
