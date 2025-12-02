package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.pojo.entity.Chat;
import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;

import java.util.List;

public interface ChatService {
    Long addChat(Long userId);
    Chat getChatEntity(Long userId, Long chatId);
    List<ChatFrontResponse> listChatsFrontResponse(Long userId);
    String getChatTitle(Long userId, Long chatId);
}
