package org.mizoguchi.misaki.mapper;

import org.mizoguchi.misaki.entity.Message;

import java.util.List;

public interface MessageMapper {
    List<Message> selectMessagesByChatId(Long chatId);
    void insertMessage(Message message);
    void updateMessage(Message message);
}
