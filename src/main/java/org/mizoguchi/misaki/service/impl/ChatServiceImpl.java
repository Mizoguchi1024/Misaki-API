package org.mizoguchi.misaki.service.impl;

import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.ChatConstant;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;
import org.mizoguchi.misaki.common.exception.ChatNotExistsException;
import org.mizoguchi.misaki.common.exception.IncompleteChatException;
import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;
import org.mizoguchi.misaki.mapper.ChatMapper;
import org.mizoguchi.misaki.mapper.MessageMapper;
import org.mizoguchi.misaki.pojo.entity.Chat;
import org.mizoguchi.misaki.pojo.entity.Message;
import org.mizoguchi.misaki.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatClient statelessChatClient;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    @Override
    public Long addChat(Long userId) {
        Chat chat = Chat.builder()
                .userId(userId)
                .build();
        chatMapper.insertChat(chat);

        return chat.getId();
    }

    @Override
    public Chat getChatEntity(Long userId, Long chatId) {
        Chat chat = chatMapper.selectChatById(chatId);
        if (chat.getUserId().equals(userId)) {
            return chat;
        }
        return null;
    }

    @Override
    public List<ChatFrontResponse> listChatsFrontResponse(Long userId) {
        List<ChatFrontResponse> chats = chatMapper.selectChatsByUserId(userId).stream()
                .map(chat -> {
                    ChatFrontResponse chatFrontResponse = new ChatFrontResponse();
                    BeanUtils.copyProperties(chat, chatFrontResponse);
                    return chatFrontResponse;
                }).collect(Collectors.toList());

        return chats;
    }



    @Override
    public String getChatTitle(Long userId, Long chatId) {
        Chat chat = getChatEntity(userId, chatId);
        if(chat == null) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }

        if (chat.getTitle() != null && !chat.getTitle().trim().isEmpty()) {
            return chat.getTitle();
        }

        List<Message> messages = messageMapper.selectMessagesByChatId(chatId);
        Message userMessage = messages.stream()
                .filter(message -> ChatConstant.TYPE_USER.equals(message.getType()))
                .findFirst()
                .orElseThrow(() -> new IncompleteChatException(FailMessageConstant.INCOMPLETE_CHAT));
        Message assistantMessage = messages.stream()
                .filter(message -> ChatConstant.TYPE_ASSISTANT.equals(message.getType()))
                .findFirst()
                .orElseThrow(() -> new IncompleteChatException(FailMessageConstant.INCOMPLETE_CHAT));

        String title = statelessChatClient.prompt()
                .system(ChatConstant.SYSTEM_GENERATE_TITLE)
                .messages(List.of(new UserMessage(userMessage.getContent()),
                        new AssistantMessage(assistantMessage.getContent())))
                .call()
                .content();

        if (title != null) {
            title = title.replaceAll(RegexConstant.QUOTE_PREFIX, "")
                    .replaceAll(RegexConstant.QUOTE_SUFFIX, "")
                    .replaceAll(RegexConstant.TITLE_INDICATION, "");
        }
        chat.setTitle(title);
        chatMapper.updateChat(chat);

        return title;
    }
}
