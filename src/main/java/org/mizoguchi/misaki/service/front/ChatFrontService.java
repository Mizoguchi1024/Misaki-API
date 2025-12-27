package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.dto.front.ListPromptsFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateChatTitleFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;

import java.util.List;

public interface ChatFrontService {
    ChatFrontResponse addChat(Long userId);
    List<ChatFrontResponse> listChats(Long userId);
    List<ChatFrontResponse> searchChats(Long userId, String keyword);
    List<String> listPrompts(Long userId, Long chatId, ListPromptsFrontRequest listPromptsFrontRequest);
    void addChatTitle(Long userId, Long chatId);
    void updateChatTitle(Long userId, Long chatId, UpdateChatTitleFrontRequest updateChatTitleFrontRequest);
    void deleteChat(Long userId, Long chatId);
    void deleteAllChats(Long userId);
}
