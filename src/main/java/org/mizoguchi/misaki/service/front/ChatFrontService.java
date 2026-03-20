package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.pojo.dto.front.ListPromptsFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateChatFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;

import java.util.List;

public interface ChatFrontService {
    ChatFrontResponse addChat(Long userId);
    PageResult<ChatFrontResponse> listChats(Long userId, Integer pageIndex, Integer pageSize);
    List<ChatFrontResponse> searchChats(Long userId, String keyword);
    List<String> listPrompts(Long userId, Long chatId, ListPromptsFrontRequest listPromptsFrontRequest);
    void addChatTitle(Long userId, Long chatId);
    void updateChat(Long userId, Long chatId, UpdateChatFrontRequest updateChatFrontRequest);
    void deleteChat(Long userId, Long chatId);
    void deleteAllChats(Long userId);
}
