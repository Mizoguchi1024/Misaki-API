package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.ChatConstant;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.enumeration.GenderEnum;
import org.mizoguchi.misaki.common.exception.AssistantNotExistsException;
import org.mizoguchi.misaki.common.exception.ChatNotExistsException;
import org.mizoguchi.misaki.common.exception.TokenNotEnoughException;
import org.mizoguchi.misaki.mapper.*;
import org.mizoguchi.misaki.pojo.dto.front.SendMessageFrontRequest;
import org.mizoguchi.misaki.pojo.entity.*;
import org.mizoguchi.misaki.pojo.vo.front.MessageFrontResponse;
import org.mizoguchi.misaki.service.front.MessageFrontService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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

        User user = userMapper.selectById(userId);

        if (user.getToken() == 0){
            throw new TokenNotEnoughException(FailMessageConstant.TOKEN_NOT_ENOUGH);
        }

        Settings settings = settingsMapper.selectOne(new LambdaQueryWrapper<Settings>().eq(Settings::getUserId, userId));
        Assistant assistant = assistantMapper.selectById(settings.getEnabledAssistantId());
        if (assistant == null || !assistant.getOwnerId().equals(userId)) {
            throw  new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        ChatClient.ChatClientRequestSpec chatClientRequestSpec = chatClient.prompt()
                .system(systemMessage -> systemMessage.params(Map.of(
                        "personality", assistant.getPersonality(),
                        "herName",assistant.getName(),
                        "username", user.getUsername(),
                        "gender", user.getGender() != GenderEnum.UNKNOWN.getCode()
                                ? "用户的性别是" + GenderEnum.fromCode(user.getGender()).getGender() + "。" : "",
                        "occupation", (user.getOccupation() != null && !user.getOccupation().isEmpty())
                                ? "用户的职业是" + user.getOccupation() + "。" : "",
                        "detail", (user.getDetail() != null && !user.getDetail().isEmpty())
                                ? "用户的详情是\"" + user.getDetail() + "\"。" : "")))
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId));

        // DeepSeek-对话前缀续写（Beta）-代码生成
        if (sendMessageFrontRequest.getPrefix() != null
                && !sendMessageFrontRequest.getPrefix().isBlank()
                && sendMessageFrontRequest.getPrefix().startsWith(ChatConstant.CODE_QUOTE)) {
            DeepSeekAssistantMessage assistantMessage = DeepSeekAssistantMessage.prefixAssistantMessage(sendMessageFrontRequest.getPrefix());
            assistantMessage.setPrefix(true);
            chatClientRequestSpec.messages(List.of(new UserMessage(sendMessageFrontRequest.getContent()), assistantMessage));
            chatClientRequestSpec.options(ChatOptions.builder().stopSequences(List.of(ChatConstant.CODE_QUOTE)).build());
        }else {
            chatClientRequestSpec.messages(new UserMessage(sendMessageFrontRequest.getContent()));
        }

        return chatClientRequestSpec
                .stream()
                .chatResponse()
                .doOnNext(chatResponse ->{
                    Usage usage = chatResponse.getMetadata().getUsage();
                    chatMapper.update(new LambdaUpdateWrapper<Chat>()
                            .eq(Chat::getId, chat.getId())
                            .set(Chat::getToken, chat.getToken() + usage.getTotalTokens()));

                    userMapper.update(new LambdaUpdateWrapper<User>()
                            .eq(User::getId, userId)
                            .set(User::getToken, Math.max(user.getToken() - usage.getTotalTokens(), 0)));
                })
                .mapNotNull(chatResponse -> chatResponse.getResult().getOutput().getText());// TODO 注意NotNull是否有问题
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
