package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.ChatConstant;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;
import org.mizoguchi.misaki.common.enumeration.MessageTypeEnum;
import org.mizoguchi.misaki.common.exception.BadAiOutputException;
import org.mizoguchi.misaki.common.exception.ChatNotExistsException;
import org.mizoguchi.misaki.common.exception.ChatTitleAlreadyExistsException;
import org.mizoguchi.misaki.common.exception.IncompleteChatException;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;
import org.mizoguchi.misaki.mapper.ChatMapper;
import org.mizoguchi.misaki.mapper.MessageMapper;
import org.mizoguchi.misaki.pojo.entity.Chat;
import org.mizoguchi.misaki.pojo.entity.Message;
import org.mizoguchi.misaki.service.front.ChatFrontService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.ResponseFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatFrontServiceImpl implements ChatFrontService {
    private final ChatClient statelessChatClient;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Long addChat(Long userId) {
        Chat chat = Chat.builder()
                .userId(userId)
                .build();

        chatMapper.insert(chat);

        return chat.getId();
    }

    @Override
    public List<ChatFrontResponse> listChats(Long userId) {
        List<Chat> chats = chatMapper.selectList(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false)
                .orderBy(true, false, Chat::getUpdateTime));

        return chats.stream().map(chat -> {
            ChatFrontResponse chatFrontResponse = new ChatFrontResponse();
            BeanUtils.copyProperties(chat, chatFrontResponse);
            return chatFrontResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ChatFrontResponse> searchChats(Long userId, String keyword) {
        List<Chat> chats = chatMapper.searchChats(userId, keyword);

        return chats.stream().map(chat -> {
            ChatFrontResponse chatFrontResponse = new ChatFrontResponse();
            BeanUtils.copyProperties(chat, chatFrontResponse);
            return chatFrontResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> listPrompts(Long userId, Long chatId, Integer size) {
        Chat chat = chatMapper.selectOne(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false));

        if(chat == null) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }

        List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getChatId, chatId));

        if (messages.isEmpty()) {
            throw new IncompleteChatException(FailMessageConstant.INCOMPLETE_CHAT);
        }

        Message assistantMessage = messages.getLast();
        if (!MessageTypeEnum.ASSISTANT.getValue().equals(assistantMessage.getType())) {
            throw new IncompleteChatException(FailMessageConstant.INCOMPLETE_CHAT);
        }

        String jsonString = statelessChatClient.prompt()
                .system(ChatConstant.SYSTEM_GENERATE_PROMPTS)
                .system(systemMessage -> systemMessage.params(Map.of("size", size)))
                .messages(new AssistantMessage(assistantMessage.getContent()))
                .options(DeepSeekChatOptions.builder()
                        .responseFormat(ResponseFormat.builder()
                                .type(ResponseFormat.Type.JSON_OBJECT)
                                .build())
                        .build())
                .call()
                .content(); // TODO Tokens计算

        JsonNode promptsNode;
        try {
            promptsNode = objectMapper.readTree(jsonString).get("prompts");
        } catch (JsonProcessingException e) {
            throw new BadAiOutputException(FailMessageConstant.BAD_AI_OUTPUT);
        }

        return objectMapper.convertValue(promptsNode, new TypeReference<>() {});
    }

    @Override
    @Transactional
    public Flux<String> addChatTitle(Long userId, Long chatId) {
        Chat chat = chatMapper.selectOne(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false));

        if(chat == null) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }

        if (chat.getTitle() != null && !chat.getTitle().isBlank()) {
            throw new ChatTitleAlreadyExistsException(FailMessageConstant.CHAT_TITLE_ALREADY_EXISTS);
        }

        List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getChatId, chatId));

        Message userMessage = messages.stream()
                .filter(message -> MessageTypeEnum.USER.getValue().equals(message.getType()))
                .findFirst()
                .orElseThrow(() -> new IncompleteChatException(FailMessageConstant.INCOMPLETE_CHAT));
        Message assistantMessage = messages.stream()
                .filter(message -> MessageTypeEnum.ASSISTANT.getValue().equals(message.getType()))
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
                    if (title != null && !title.isBlank()) {
                        title = title.replaceAll(RegexConstant.QUOTE_PREFIX, "")
                                .replaceAll(RegexConstant.QUOTE_SUFFIX, "")
                                .replaceAll(RegexConstant.TITLE_INDICATION, "");

                        chatMapper.update(new LambdaUpdateWrapper<Chat>()
                                .eq(Chat::getId, chat.getId())
                                .set(Chat::getToken, chat.getToken() + usage.getTotalTokens())
                                .set(Chat::getTitle, title));

                        User user = userMapper.selectById(userId);

                        userMapper.update(new LambdaUpdateWrapper<User>()
                                .eq(User::getId, userId)
                                .set(User::getToken, Math.max(user.getToken() - usage.getTotalTokens(), 0)));
                    }
                })
                .mapNotNull(chatResponse -> chatResponse.getResult().getOutput().getText());// TODO 注意NotNull是否有问题;
    }

    @Override
    public void updateChatTitle(Long userId, Long chatId, String title) {
        int affectedRows = chatMapper.update(new LambdaUpdateWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false)
                .set(Chat::getTitle, title));

        if(affectedRows == 0) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }
    }

    @Override
    public void deleteChat(Long userId, Long chatId) {
        int affectedRows = chatMapper.update(new LambdaUpdateWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false)
                .set(Chat::getDeleteFlag, true));

        if(affectedRows == 0) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }
    }

    @Override
    public void deleteAllChats(Long userId) {
        int affectedRows = chatMapper.update(new LambdaUpdateWrapper<Chat>()
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false)
                .set(Chat::getDeleteFlag, true));

        if(affectedRows == 0) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }
    }
}
