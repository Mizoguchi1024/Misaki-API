package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Chat;
import org.mizoguchi.misaki.entity.Message;
import org.mizoguchi.misaki.entity.vo.front.ChatFrontResponse;
import org.mizoguchi.misaki.entity.vo.front.MessageFrontResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    Long addChat(Long userId);
    Chat getChatEntity(Long userId, Long chatId);
    List<ChatFrontResponse> listChatsFrontResponse(Long userId);
    String getChatTitle(Long userId, Long chatId);

    Flux<String> sendMessage(Long userId, Long chatId, String content, String prefix);
    List<Message> listMessagesEntity(Long userId, Long chatId);
    List<MessageFrontResponse> listMessagesFrontResponse(Long userId, Long chatId);
}
