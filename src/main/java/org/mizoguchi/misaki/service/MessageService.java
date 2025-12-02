package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.pojo.entity.Message;
import org.mizoguchi.misaki.pojo.vo.front.MessageFrontResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MessageService {
    Flux<String> sendMessage(Long userId, Long chatId, String content, String prefix);
    List<Message> listMessagesEntity(Long userId, Long chatId);
    List<MessageFrontResponse> listMessagesFrontResponse(Long userId, Long chatId);
}
