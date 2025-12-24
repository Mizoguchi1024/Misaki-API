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
import org.mizoguchi.misaki.common.exception.*;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.pojo.dto.front.ListPromptsFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateChatTitleFrontRequest;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;
import org.mizoguchi.misaki.mapper.ChatMapper;
import org.mizoguchi.misaki.mapper.MessageMapper;
import org.mizoguchi.misaki.pojo.entity.Chat;
import org.mizoguchi.misaki.pojo.entity.Message;
import org.mizoguchi.misaki.service.front.ChatFrontService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.ResponseFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatFrontServiceImpl implements ChatFrontService {
    private final ChatClient chatClient;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Long addChat(Long userId) {
        Chat chat = Chat.builder()
                .userId(userId)
                .token(0)
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
    public List<String> listPrompts(Long userId, Long chatId, ListPromptsFrontRequest listPromptsFrontRequest) {
        if(!chatMapper.exists(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false)
        )) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }

        Map<String, Object> advisorParams = new HashMap<>();
        advisorParams.put(ChatConstant.CONVERSATION_ID, chatId);
        advisorParams.put(ChatConstant.DISABLE_DB_WRITE, true);

        if (!messageMapper.exists(new LambdaQueryWrapper<Message>()
                .eq(Message::getId, listPromptsFrontRequest.getParentId())
                .eq(Message::getChatId, chatId)
        )) {
                throw new MessageNotExistsException(FailMessageConstant.MESSAGE_NOT_EXISTS);
        }

        advisorParams.put(ChatConstant.PARENT_ID, listPromptsFrontRequest.getParentId());

        User user = userMapper.selectById(userId);
        if (user.getToken() <= 0){
            throw new TokenNotEnoughException(FailMessageConstant.TOKEN_NOT_ENOUGH);
        }

        List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getChatId, chatId)
        );

        if (messages.isEmpty()) {
            throw new IncompleteChatException(FailMessageConstant.INCOMPLETE_CHAT);
        }

        Message assistantMessage = messages.getLast();
        if (!MessageType.ASSISTANT.getValue().equalsIgnoreCase(assistantMessage.getType())) {
            throw new IncompleteChatException(FailMessageConstant.INCOMPLETE_CHAT);
        }

        ChatResponse chatResponse = chatClient.prompt()
                .system(ChatConstant.SYSTEM_GENERATE_PROMPTS)
                .system(systemMessage -> systemMessage.params(Map.of(ChatConstant.SIZE, listPromptsFrontRequest.getSize())))
                .advisors(advisorSpec -> advisorSpec.params(advisorParams))
                .options(DeepSeekChatOptions.builder()
                        .responseFormat(ResponseFormat.builder()
                                .type(ResponseFormat.Type.JSON_OBJECT)
                                .build())
                        .build())
                .call()
                .chatResponse();

        if (chatResponse == null) {
            throw new BadAiOutputException(FailMessageConstant.BAD_AI_OUTPUT);
        }

        Usage usage = chatResponse.getMetadata().getUsage();
        chatMapper.update(new LambdaUpdateWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .setIncrBy(Chat::getToken, usage.getTotalTokens())
                .setIncrBy(Chat::getVersion, 1)
        );

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .setDecrBy(User::getToken, usage.getTotalTokens())
                .setIncrBy(User::getVersion, 1)
        );

        String jsonString = chatResponse.getResult().getOutput().getText();
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
    public void addChatTitle(Long userId, Long chatId) {
        Chat chat = chatMapper.selectOne(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false));

        if(chat == null) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }

        if (StringUtils.hasText(chat.getTitle())) {
            throw new ChatTitleAlreadyExistsException(FailMessageConstant.CHAT_TITLE_ALREADY_EXISTS);
        }

        User user = userMapper.selectById(userId);
        if (user.getToken() <= 0){
            throw new TokenNotEnoughException(FailMessageConstant.TOKEN_NOT_ENOUGH);
        }

        List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getChatId, chatId)
                .orderBy(true, true, Message::getCreateTime));

        Message userMessage = messages.stream()
                .filter(message -> MessageType.USER.getValue().equalsIgnoreCase(message.getType()))
                .findFirst()
                .orElseThrow(() -> new IncompleteChatException(FailMessageConstant.INCOMPLETE_CHAT));
        Message assistantMessage = messages.stream()
                .filter(message -> MessageType.ASSISTANT.getValue().equalsIgnoreCase(message.getType()))
                .findFirst()
                .orElseThrow(() -> new IncompleteChatException(FailMessageConstant.INCOMPLETE_CHAT));

        ChatResponse chatResponse = chatClient.prompt()
                .system(ChatConstant.SYSTEM_GENERATE_TITLE)
                .messages(List.of(new UserMessage(userMessage.getContent()),
                        new AssistantMessage(assistantMessage.getContent())))
                .call()
                .chatResponse();

        if (chatResponse == null) {
            throw new BadAiOutputException(FailMessageConstant.BAD_AI_OUTPUT);
        }

        Usage usage = chatResponse.getMetadata().getUsage();
        String title = chatResponse.getResult().getOutput().getText();

        if (StringUtils.hasText(title)) {
            chatMapper.update(new LambdaUpdateWrapper<Chat>()
                    .eq(Chat::getId, chatId)
                    .set(Chat::getTitle, title)
                    .setIncrBy(Chat::getToken, usage.getTotalTokens())
                    .setIncrBy(Chat::getVersion, 1)
            );

            userMapper.update(new LambdaUpdateWrapper<User>()
                    .eq(User::getId, userId)
                    .setDecrBy(User::getToken, usage.getTotalTokens())
                    .setIncrBy(User::getVersion, 1)
            );
        }
    }

    @Override
    public void updateChatTitle(Long userId, Long chatId, UpdateChatTitleFrontRequest updateChatTitleFrontRequest) {
        Chat chat = chatMapper.selectOne(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false));

        if(chat == null) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }

        BeanUtils.copyProperties(updateChatTitleFrontRequest, chat);
        int affectedRows = chatMapper.updateById(chat);

        if(affectedRows == 0) {
            throw new OptimisticLockFailedException(FailMessageConstant.OPTIMISTIC_LOCK_FAILED);
        }
    }

    @Override
    public void deleteChat(Long userId, Long chatId) {
        int affectedRows = chatMapper.update(new LambdaUpdateWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false)
                .set(Chat::getDeleteFlag, true)
                .setIncrBy(Chat::getVersion, 1)
        );

        if(affectedRows == 0) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }
    }

    @Override
    public void deleteAllChats(Long userId) {
        int affectedRows = chatMapper.update(new LambdaUpdateWrapper<Chat>()
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false)
                .set(Chat::getDeleteFlag, true)
                .setIncrBy(Chat::getVersion, 1)
        );

        if(affectedRows == 0) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }
    }
}
