package org.mizoguchi.misaki.mapper;

import org.mizoguchi.misaki.pojo.entity.Chat;

import java.util.List;

public interface ChatMapper {
    Chat selectChatById(Long id);
    List<Chat> selectChatsByUserId(Long userId);
    void insertChat(Chat chat);
    void updateChat(Chat chat);
}
