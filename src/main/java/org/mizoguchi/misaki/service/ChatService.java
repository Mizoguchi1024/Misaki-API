package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    Long addChat(Long userId);
    List<ChatFrontResponse> listChatsFrontResponse(Long userId);
    Flux<String> addChatTitle(Long userId, Long chatId);
    void deleteChat(Long userId, Long chatId);
}
