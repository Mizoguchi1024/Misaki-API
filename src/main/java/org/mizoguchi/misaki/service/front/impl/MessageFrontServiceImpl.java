package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.ChatConstant;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.enumeration.GenderEnum;
import org.mizoguchi.misaki.common.exception.AssistantNotExistsException;
import org.mizoguchi.misaki.common.exception.ChatNotExistsException;
import org.mizoguchi.misaki.common.exception.MessageNotExistsException;
import org.mizoguchi.misaki.common.exception.TokenNotEnoughException;
import org.mizoguchi.misaki.mapper.*;
import org.mizoguchi.misaki.pojo.dto.front.SendMessageFrontRequest;
import org.mizoguchi.misaki.pojo.entity.*;
import org.mizoguchi.misaki.pojo.vo.front.MessageFrontResponse;
import org.mizoguchi.misaki.service.front.MessageFrontService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageFrontServiceImpl implements MessageFrontService {
    private final ChatClient chatClient;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final SettingsMapper settingsMapper;
    private final AssistantMapper assistantMapper;

    @Override
    public Flux<String> sendMessage(Long userId, Long chatId, SendMessageFrontRequest sendMessageFrontRequest) {
        Chat chat = chatMapper.selectOne(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId));

        if (chat == null) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }

        Map<String, Object> advisorParams = new HashMap<>();
        advisorParams.put(ChatConstant.CONVERSATION_ID, chatId);

        if (sendMessageFrontRequest.getParentId() != null) {
            if (!messageMapper.exists(new LambdaQueryWrapper<Message>()
                    .eq(Message::getId, sendMessageFrontRequest.getParentId())
                    .eq(Message::getChatId, chatId)
            )) {
                throw new MessageNotExistsException(FailMessageConstant.MESSAGE_NOT_EXISTS);
            }

            advisorParams.put(ChatConstant.PARENT_ID, sendMessageFrontRequest.getParentId());
        }

        User user = userMapper.selectById(userId);

        if (user.getToken() <= 0){
            throw new TokenNotEnoughException(FailMessageConstant.TOKEN_NOT_ENOUGH);
        }

        Settings settings = settingsMapper.selectOne(new LambdaQueryWrapper<Settings>().eq(Settings::getUserId, userId));
        Assistant assistant = assistantMapper.selectById(settings.getEnabledAssistantId());
        if (assistant == null || !assistant.getOwnerId().equals(userId)) {
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        Map<String, Object> systemMessageParams = new HashMap<>();
        systemMessageParams.put(ChatConstant.ASSISTANT_NAME, assistant.getName());
        systemMessageParams.put(ChatConstant.ASSISTANT_GENDER, GenderEnum.fromCode(assistant.getGender()).getGender());
        systemMessageParams.put(ChatConstant.ASSISTANT_BIRTHDAY, assistant.getBirthday());
        systemMessageParams.put(ChatConstant.ASSISTANT_PERSONALITY, assistant.getPersonality());
        systemMessageParams.put(ChatConstant.USER_NAME, user.getUsername());
        systemMessageParams.put(ChatConstant.USER_GENDER, GenderEnum.fromCode(user.getGender()).getGender());
        systemMessageParams.put(ChatConstant.USER_BIRTHDAY, user.getBirthday());
        systemMessageParams.put(ChatConstant.USER_OCCUPATION, user.getOccupation());
        systemMessageParams.put(ChatConstant.USER_DETAIL, user.getDetail());
        systemMessageParams.replaceAll((k, v) -> v == null ? "" : v);

        ChatClient.ChatClientRequestSpec chatClientRequestSpec = chatClient.prompt()
                .system(ChatConstant.SYSTEM_DEFAULT)
                .system(systemMessage -> systemMessage.params(systemMessageParams))
                .advisors(advisorSpec -> advisorSpec.params(advisorParams));

        // DeepSeek-对话前缀续写（Beta）
        String prefix = sendMessageFrontRequest.getPrefix();
        UserMessage userMessage = new UserMessage(sendMessageFrontRequest.getContent());
        if (prefix != null) {
            DeepSeekAssistantMessage assistantMessage = new DeepSeekAssistantMessage.Builder()
                    .content(prefix)
                    .prefix(true)
                    .build();

            chatClientRequestSpec.messages(List.of(userMessage, assistantMessage));
            if (prefix.startsWith(ChatConstant.CODE_QUOTE)) {
                chatClientRequestSpec.options(ChatOptions.builder()
                        .stopSequences(List.of(ChatConstant.CODE_QUOTE))
                        .build()
                );
            }
        }else {
            chatClientRequestSpec.messages(userMessage);
        }

        Flux<ChatResponse> responseFlux = chatClientRequestSpec
                .stream()
                .chatResponse()
                .cache(); // 复用同一个流

        Mono<Long> tokenMono = responseFlux.map(chatResponse -> {
                    Usage usage = chatResponse.getMetadata().getUsage();
                    return usage == null ? 0L : usage.getTotalTokens();
                })
                .reduce(0L, Long::sum);

        return responseFlux.mapNotNull(chatResponse -> chatResponse.getResult().getOutput().getText())
                .doOnComplete(() -> tokenMono.subscribe(tokens -> {
                    if (tokens > 0) {
                        chatMapper.update(new LambdaUpdateWrapper<Chat>()
                                .eq(Chat::getId, chatId)
                                .setIncrBy(Chat::getToken, tokens)
                                .setIncrBy(Chat::getVersion, 1)
                        );

                        userMapper.update(new LambdaUpdateWrapper<User>()
                                .eq(User::getId, userId)
                                .setDecrBy(User::getToken, tokens)
                                .setIncrBy(User::getVersion, 1)
                        );
                    }
                }));
    }

    @Override
    public List<MessageFrontResponse> listMessages(Long userId, Long chatId) {
        Chat chat = chatMapper.selectOne(new LambdaQueryWrapper<Chat>()
                .eq(Chat::getId, chatId)
                .eq(Chat::getUserId, userId));

        if (chat == null) {
            throw new ChatNotExistsException(FailMessageConstant.CHAT_NOT_EXISTS);
        }

        List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<Message>().eq(Message::getChatId, chatId));

        return messages.stream().map(message -> {
            MessageFrontResponse messageFrontResponse = new MessageFrontResponse();
            BeanUtils.copyProperties(message, messageFrontResponse);
            return messageFrontResponse;
        }).collect(Collectors.toList());
    }
}
