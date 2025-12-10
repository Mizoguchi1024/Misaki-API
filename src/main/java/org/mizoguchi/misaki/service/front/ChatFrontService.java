package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatFrontService {
    Long addChat(Long userId);
    List<ChatFrontResponse> listChats(Long userId);
    List<ChatFrontResponse> searchChats(Long userId, String keyword);
    Flux<String> addChatTitle(Long userId, Long chatId);
    void updateChatTitle(Long userId, String chatId, String title);
    void deleteChat(Long userId, Long chatId);
}
