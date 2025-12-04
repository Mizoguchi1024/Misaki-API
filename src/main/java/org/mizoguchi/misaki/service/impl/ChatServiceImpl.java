package org.mizoguchi.misaki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.ChatConstant;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;
import org.mizoguchi.misaki.common.exception.ChatNotExistsException;
import org.mizoguchi.misaki.common.exception.ChatTitleAlreadyExistsException;
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
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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

        chatMapper.insert(chat);

        return chat.getId();
    }

    @Override
    public List<ChatFrontResponse> listChatsFrontResponse(Long userId) {
        List<Chat> chats = chatMapper.selectList(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false));

        return chats.stream().map(chat -> {
            ChatFrontResponse chatFrontResponse = new ChatFrontResponse();
            BeanUtils.copyProperties(chat, chatFrontResponse);
            return chatFrontResponse;
        }).collect(Collectors.toList());
    }



    @Override
    public Flux<String> addChatTitle(Long userId, Long chatId) {
        Chat chat = chatMapper.selectOne(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId));

        if(chat == null) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }

        if (chat.getTitle() != null && !chat.getTitle().trim().isEmpty()) {
            throw new ChatTitleAlreadyExistsException(FailMessageConstant.CHAT_TITLE_ALREADY_EXISTS);
        }

        List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getChatId, chatId));

        Message userMessage = messages.stream()
                .filter(message -> ChatConstant.TYPE_USER.equals(message.getType()))
                .findFirst()
                .orElseThrow(() -> new IncompleteChatException(FailMessageConstant.INCOMPLETE_CHAT));
        Message assistantMessage = messages.stream()
                .filter(message -> ChatConstant.TYPE_ASSISTANT.equals(message.getType()))
                .findFirst()
                .orElseThrow(() -> new IncompleteChatException(FailMessageConstant.INCOMPLETE_CHAT));

        return statelessChatClient.prompt()
                .system(ChatConstant.SYSTEM_GENERATE_TITLE)
                .messages(List.of(new UserMessage(userMessage.getContent()),
                        new AssistantMessage(assistantMessage.getContent())))
                .stream()
                .chatResponse()
                .doOnNext(chatResponse -> {
                    Usage usage = chatResponse.getMetadata().getUsage();
                    String title = chatResponse.getResult().getOutput().getText();
                    if (title != null && !title.trim().isEmpty()) {
                        chat.setToken(chat.getToken() + usage.getTotalTokens());
                        chat.setTitle(title.replaceAll(RegexConstant.QUOTE_PREFIX, "")
                                .replaceAll(RegexConstant.QUOTE_SUFFIX, "")
                                .replaceAll(RegexConstant.TITLE_INDICATION, ""));

                        chatMapper.updateById(chat);
                    }
                })
                .mapNotNull(chatResponse -> chatResponse.getResult().getOutput().getText());// TODO 注意NotNull是否有问题;
    }
}
