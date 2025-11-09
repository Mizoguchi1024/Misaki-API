package org.mizoguchi.misaki.mapper;

import org.mizoguchi.misaki.entity.Conversation;

import java.util.List;

public interface ConversationMapper {
    Conversation selectConversationByIdAndUserId(Long id, Long userId);
    List<Conversation> selectConversationsByUserId(Long userId);
    void insertConversation(Conversation conversation);
    void updateConversation(Conversation conversation);
}
