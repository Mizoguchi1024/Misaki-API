package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.ChatConstant;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.JsonConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;
import org.mizoguchi.misaki.common.enumeration.GenderEnum;
import org.mizoguchi.misaki.common.exception.*;
import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.pojo.dto.front.ListPromptsFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateChatFrontRequest;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.ChatTitleFrontResponse;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.mapper.ChatMapper;
import org.mizoguchi.misaki.mapper.MessageMapper;
import org.mizoguchi.misaki.mapper.SettingsMapper;
import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.entity.Chat;
import org.mizoguchi.misaki.pojo.entity.Message;
import org.mizoguchi.misaki.pojo.entity.Settings;
import org.mizoguchi.misaki.service.front.ChatFrontService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.MessageType;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatFrontServiceImpl implements ChatFrontService {
    private final ChatClient chatClient;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final SettingsMapper settingsMapper;
    private final AssistantMapper assistantMapper;
    private final ObjectMapper objectMapper;

    @Override
    public ChatFrontResponse addChat(Long userId) {
        Chat chat = Chat.builder()
                .userId(userId)
                .token(0)
                .pinnedFlag(false)
                .build();

        chatMapper.insert(chat);

        ChatFrontResponse chatFrontResponse = new ChatFrontResponse();
        BeanUtils.copyProperties(chat, chatFrontResponse);
        return chatFrontResponse;
    }

    @Override
    public PageResult<ChatFrontResponse> listChats(Long userId, Integer pageIndex, Integer pageSize) {
        IPage<Chat> chatsPage = chatMapper.selectPage(new Page<>(pageIndex, pageSize), new LambdaQueryWrapper<Chat>()
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false)
                .orderByDesc(true, Chat::getUpdateTime)
        );

        PageResult<ChatFrontResponse>  pageResult = new PageResult<>();
        pageResult.setList(chatsPage.getRecords().stream()
                .map(chat -> {
                    ChatFrontResponse chatFrontResponse = new ChatFrontResponse();
                    BeanUtils.copyProperties(chat, chatFrontResponse);

                    return chatFrontResponse;
                }).collect(Collectors.toList()));

        pageResult.setTotal(chatsPage.getTotal());
        pageResult.setPageIndex(chatsPage.getCurrent());
        pageResult.setPageSize(chatsPage.getSize());

        return pageResult;
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
        boolean chatExistsFlag = chatMapper.exists(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false)
        );

        if(!chatExistsFlag) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }

        Map<String, Object> advisorParams = new HashMap<>();
        advisorParams.put(ChatConstant.CONVERSATION_ID, chatId);
        advisorParams.put(ChatConstant.DISABLE_DB_WRITE, true);

        boolean parentMessageExistsFlag = messageMapper.exists(new LambdaQueryWrapper<Message>()
                .eq(Message::getId, listPromptsFrontRequest.getParentId())
                .eq(Message::getChatId, chatId)
        );

        if (!parentMessageExistsFlag) {
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

        Settings settings = settingsMapper
                .selectOne(new LambdaQueryWrapper<Settings>().eq(Settings::getUserId, userId));
        Assistant assistant = assistantMapper.selectById(settings.getEnabledAssistantId());
        if (assistant == null || !assistant.getOwnerId().equals(userId)) {
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        Map<String, Object> systemMessageParams = new HashMap<>();
        systemMessageParams.put(ChatConstant.SIZE, listPromptsFrontRequest.getSize());
        systemMessageParams.put(ChatConstant.ASSISTANT_NAME, assistant.getName());
        systemMessageParams.put(ChatConstant.ASSISTANT_GENDER, GenderEnum.fromCode(assistant.getGender()).getGender());
        systemMessageParams.put(ChatConstant.ASSISTANT_BIRTHDAY, assistant.getBirthday());
        systemMessageParams.put(ChatConstant.ASSISTANT_PERSONALITY, assistant.getPersonality());
        systemMessageParams.put(ChatConstant.ASSISTANT_DETAIL, assistant.getDetail());
        systemMessageParams.put(ChatConstant.USER_NAME, user.getUsername());
        systemMessageParams.put(ChatConstant.USER_GENDER, GenderEnum.fromCode(user.getGender()).getGender());
        systemMessageParams.put(ChatConstant.USER_BIRTHDAY, user.getBirthday());
        systemMessageParams.put(ChatConstant.USER_OCCUPATION, user.getOccupation());
        systemMessageParams.put(ChatConstant.USER_DETAIL, user.getDetail());
        systemMessageParams.replaceAll((k, v) -> v == null ? "" : v);

        ChatResponse chatResponse = chatClient.prompt()
                .system(ChatConstant.SYSTEM_GENERATE_PROMPTS)
                .system(systemMessage -> systemMessage.params(systemMessageParams))
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
            promptsNode = objectMapper.readTree(jsonString).get(JsonConstant.PROMPTS_NODE);
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

        Map<String, Object> advisorParams = new HashMap<>();
        advisorParams.put(ChatConstant.CONVERSATION_ID, chatId);

        List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getChatId, chatId)
        );

        Message assistantMessage = messages.stream()
                .filter(message -> MessageType.ASSISTANT.getValue().equalsIgnoreCase(message.getType()))
                .findFirst()
                .orElseThrow(() -> new IncompleteChatException(FailMessageConstant.INCOMPLETE_CHAT));

        advisorParams.put(ChatConstant.PARENT_ID, assistantMessage.getId());
        advisorParams.put(ChatConstant.DISABLE_DB_WRITE, true);

        ChatResponse chatResponse = chatClient.prompt()
                .system(ChatConstant.SYSTEM_GENERATE_TITLE)
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
        ChatTitleFrontResponse chatTitleFrontResponse;
        try {
            chatTitleFrontResponse = objectMapper.readValue(chatResponse.getResult().getOutput().getText(), ChatTitleFrontResponse.class);
        } catch (JsonProcessingException e) {
            throw new BadAiOutputException(FailMessageConstant.BAD_AI_OUTPUT);
        }
        String title = chatTitleFrontResponse.getTitle();

        if (!StringUtils.hasText(title) || !isTitleLengthValid(title)) {
            throw new BadAiOutputException(FailMessageConstant.BAD_AI_OUTPUT);
        }

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

    @Override
    public void updateChat(Long userId, Long chatId, UpdateChatFrontRequest updateChatFrontRequest) {
        Chat chat = chatMapper.selectOne(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId)
                .eq(Chat::getDeleteFlag, false));

        if(chat == null) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }

        BeanUtils.copyProperties(updateChatFrontRequest, chat);
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

    public boolean isTitleLengthValid(String title) {
        int cjkCount = 0;
        int engCount = 0;

        // 中文按字算
        Pattern hanPattern = Pattern.compile(RegexConstant.CJK_CHARACTER);
        Matcher hanMatcher = hanPattern.matcher(title);
        while (hanMatcher.find()) {
            cjkCount++;
        }

        // 英文按词算
        Pattern engPattern = Pattern.compile(RegexConstant.ENGLISH_WORD);
        Matcher engMatcher = engPattern.matcher(title);
        while (engMatcher.find()) {
            engCount++;
        }

        return engCount <= 10 && cjkCount + engCount <= 20;
    }
}
