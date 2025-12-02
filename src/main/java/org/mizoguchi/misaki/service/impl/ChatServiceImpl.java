package org.mizoguchi.misaki.service.impl;

import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.ChatConstant;
import org.mizoguchi.misaki.common.constant.MessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;
import org.mizoguchi.misaki.common.enumeration.GenderEnum;
import org.mizoguchi.misaki.common.exception.ChatNotExistsException;
import org.mizoguchi.misaki.common.exception.IncompleteChatException;
import org.mizoguchi.misaki.entity.*;
import org.mizoguchi.misaki.entity.vo.front.ChatFrontResponse;
import org.mizoguchi.misaki.entity.vo.front.MessageFrontResponse;
import org.mizoguchi.misaki.mapper.ChatMapper;
import org.mizoguchi.misaki.mapper.MessageMapper;
import org.mizoguchi.misaki.service.AssistantService;
import org.mizoguchi.misaki.service.ChatService;
import org.mizoguchi.misaki.service.UserService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
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
public class ChatServiceImpl implements ChatService {
    private final ChatClient chatClient;
    private final ChatClient statelessChatClient;
    private final UserService userService;
    private final AssistantService assistantService;
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
    public Flux<String> sendMessage(Long userId, Long chatId, String content, String prefix) {
        if(getChatEntity(userId, chatId) == null) {
            throw new ChatNotExistsException(MessageConstant.CHAT_NOT_EXISTS);
        }

        User user = userService.getUserEntity(userId);
        Settings settings = userService.getSettingEntity(userId);
        Assistant assistant = assistantService.getAssistantEntity(userId, settings.getEnabledAssistantId());

        ChatClient.ChatClientRequestSpec chatClientRequestSpec = chatClient.prompt()
                .system(sp -> sp.params(Map.of(
                        "personality", assistant.getPersonality(),
                        "herName",assistant.getName(),
                        "username", user.getUsername(),
                        "gender", user.getGender() != GenderEnum.UNKNOWN.getCode()
                                ? "用户的性别是" + GenderEnum.fromCode(user.getGender()).getGender() + "。" : "",
                        "occupation", (user.getOccupation() != null && !user.getOccupation().isEmpty())
                                ? "用户的职业是" + user.getOccupation() + "。" : "",
                        "detail", (user.getDetail() != null && !user.getDetail().isEmpty())
                                ? "用户的详情是\"" + user.getDetail() + "\"。" : "")))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId));

        // DeepSeek-对话前缀续写（Beta）-代码生成
        if (prefix != null && !prefix.trim().isEmpty() && prefix.startsWith(ChatConstant.CODE_QUOTE)) {
            DeepSeekAssistantMessage assistantMessage = DeepSeekAssistantMessage.prefixAssistantMessage(prefix);
            assistantMessage.setPrefix(true);
            chatClientRequestSpec.messages(List.of(new UserMessage(content), assistantMessage));
            chatClientRequestSpec.options(ChatOptions.builder().stopSequences(List.of(ChatConstant.CODE_QUOTE)).build());
        }else {
            chatClientRequestSpec.messages(new UserMessage(content));
        }

        return chatClientRequestSpec.stream().content();
    }

    @Override
    public List<Message> listMessagesEntity(Long userId, Long chatId) {
        if(getChatEntity(userId, chatId) == null) {
            throw new ChatNotExistsException(MessageConstant.CHAT_NOT_EXISTS);
        }

        return messageMapper.selectMessagesByChatId(chatId);
    }

    @Override
    public List<MessageFrontResponse> listMessagesFrontResponse(Long userId, Long chatId) {
        List<MessageFrontResponse> messages = listMessagesEntity(userId, chatId).stream()
                .map(message -> {
                    MessageFrontResponse messageFrontResponse = new MessageFrontResponse();
                    BeanUtils.copyProperties(message, messageFrontResponse);
                    return messageFrontResponse;
                }).collect(Collectors.toList());

        return messages;
    }

    @Override
    public String getChatTitle(Long userId, Long chatId) {
        Chat chat = getChatEntity(userId, chatId);
        if(chat == null) {
            throw new ChatNotExistsException(MessageConstant.CHAT_NOT_EXISTS);
        }

        if (chat.getTitle() != null && !chat.getTitle().trim().isEmpty()) {
            return chat.getTitle();
        }

        List<Message> messages = listMessagesEntity(userId, chatId);
        Message userMessage = messages.stream()
                .filter(message -> ChatConstant.TYPE_USER.equals(message.getType()))
                .findFirst()
                .orElseThrow(() -> new IncompleteChatException(MessageConstant.INCOMPLETE_CHAT));
        Message assistantMessage = messages.stream()
                .filter(message -> ChatConstant.TYPE_ASSISTANT.equals(message.getType()))
                .findFirst()
                .orElseThrow(() -> new IncompleteChatException(MessageConstant.INCOMPLETE_CHAT));

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
