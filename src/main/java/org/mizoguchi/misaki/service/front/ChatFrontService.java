package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.dto.front.UpdateChatTitleFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatFrontService {
    Long addChat(Long userId);
    List<ChatFrontResponse> listChats(Long userId);
    List<ChatFrontResponse> searchChats(Long userId, String keyword);
    List<String> listPrompts(Long userId, Long chatId, Integer size);
    Flux<String> addChatTitle(Long userId, Long chatId);
    void updateChatTitle(Long userId, Long chatId, UpdateChatTitleFrontRequest updateChatTitleFrontRequest);
    void deleteChat(Long userId, Long chatId);
    void deleteAllChats(Long userId);
}
