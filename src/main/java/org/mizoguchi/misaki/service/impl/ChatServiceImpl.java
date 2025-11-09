package org.mizoguchi.misaki.service.impl;

import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.ChatConstant;
import org.mizoguchi.misaki.common.constant.MessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;
import org.mizoguchi.misaki.common.enumeration.GenderEnum;
import org.mizoguchi.misaki.common.exception.ConversationNotExistsException;
import org.mizoguchi.misaki.common.exception.IncompleteConversationException;
import org.mizoguchi.misaki.entity.Conversation;
import org.mizoguchi.misaki.entity.Message;
import org.mizoguchi.misaki.entity.Setting;
import org.mizoguchi.misaki.entity.User;
import org.mizoguchi.misaki.mapper.ConversationMapper;
import org.mizoguchi.misaki.mapper.MessageMapper;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.service.ChatService;
import org.mizoguchi.misaki.service.UserService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatClient chatClient;
    private final ChatClient statelessChatClient;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;

    @Override
    public Long createConversation(String email) {
        Long userId = userMapper.selectUserByEmail(email).getId();
        Conversation conversation = Conversation.builder()
                .userId(userId)
                .build();
        conversationMapper.insertConversation(conversation);

        return conversation.getId();
    }

    @Override
    public Conversation getConversation(String email, Long conversationId) {
        Long userId = userMapper.selectUserByEmail(email).getId();

        return conversationMapper.selectConversationByIdAndUserId(conversationId, userId);
    }

    @Override
    public List<Conversation> getConversations(String email) {
        Long userId = userMapper.selectUserByEmail(email).getId();

        return conversationMapper.selectConversationsByUserId(userId);
    }

    @Override
    public Flux<String> sendMessage(String email, Long conversationId, String content, String prefix) {
        if(getConversation(email, conversationId) == null) {
            throw new ConversationNotExistsException(MessageConstant.CONVERSATION_NOT_EXISTS);
        }

        User user = userService.getProfile(email);
        Setting setting = userService.getSetting(email);

        ChatClient.ChatClientRequestSpec chatClientRequestSpec = chatClient.prompt()
                .system(sp -> sp.params(Map.of(
                        "personality", setting.getPersonality(),
                        "herName",setting.getHerName(),
                        "username", user.getUsername(),
                        "gender", user.getGender() != GenderEnum.UNKNOWN.getCode()
                                ? "用户的性别是" + GenderEnum.fromCode(user.getGender()).getGender() + "。" : "",
                        "occupation", (user.getOccupation() != null && !user.getOccupation().isEmpty())
                                ? "用户的职业是" + user.getOccupation() + "。" : "",
                        "detail", (user.getDetail() != null && !user.getDetail().isEmpty())
                                ? "用户的详情是\"" + user.getDetail() + "\"。" : "")))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId));

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
    public List<Message> getMessages(String email, Long conversationId) {
        if(getConversation(email, conversationId) == null) {
            throw new ConversationNotExistsException(MessageConstant.CONVERSATION_NOT_EXISTS);
        }

        return messageMapper.selectMessagesByConversationId(conversationId);
    }

    @Override
    public String getTitle(String email, Long conversationId) {
        Conversation conversation = getConversation(email, conversationId);
        if(conversation == null) {
            throw new ConversationNotExistsException(MessageConstant.CONVERSATION_NOT_EXISTS);
        }

        if (conversation.getTitle() != null && !conversation.getTitle().trim().isEmpty()) {
            return conversation.getTitle();
        }

        List<Message> messages = getMessages(email, conversationId);
        Message userMessage = messages.stream()
                .filter(message -> ChatConstant.TYPE_USER.equals(message.getType()))
                .findFirst()
                .orElseThrow(() -> new IncompleteConversationException(MessageConstant.INCOMPLETE_CONVERSATION));
        Message assistantMessage = messages.stream()
                .filter(message -> ChatConstant.TYPE_ASSISTANT.equals(message.getType()))
                .findFirst()
                .orElseThrow(() -> new IncompleteConversationException(MessageConstant.INCOMPLETE_CONVERSATION));

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
        conversation.setTitle(title);
        conversationMapper.updateConversation(conversation);

        return title;
    }
}
