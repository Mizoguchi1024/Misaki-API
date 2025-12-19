package org.mizoguchi.misaki.advisor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Getter;
import org.mizoguchi.misaki.common.constant.ChatConstant;
import org.mizoguchi.misaki.mapper.MessageMapper;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.BaseChatMemoryAdvisor;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.Generation;
import org.springframework.util.Assert;
import reactor.core.scheduler.Scheduler;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class TreeMemoryAdvisor implements BaseChatMemoryAdvisor {

    private final MessageMapper messageMapper;
    private final int order;
    private final Scheduler scheduler;

    private TreeMemoryAdvisor(MessageMapper messageMapper, int order, Scheduler scheduler) {
        Assert.notNull(messageMapper, "messageMapper cannot be null");
        Assert.notNull(scheduler, "scheduler cannot be null");
        this.messageMapper = messageMapper;
        this.order = order;
        this.scheduler = scheduler;
    }

    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String conversationId = getConversationId(chatClientRequest.context());
        String parentId = getParentId(chatClientRequest.context());

        // conversationId == null → 不开启聊天记忆
        if (conversationId == null) {
            return chatClientRequest;
        }

        List<org.mizoguchi.misaki.pojo.entity.Message> historyEntities = List.of();

        // parentId != null 才需要回溯历史
        if (parentId != null) {
            List<org.mizoguchi.misaki.pojo.entity.Message> allMessages =
                    this.messageMapper.selectList(
                            new LambdaQueryWrapper<org.mizoguchi.misaki.pojo.entity.Message>()
                                    .eq(org.mizoguchi.misaki.pojo.entity.Message::getChatId, Long.valueOf(conversationId))
                    );

            historyEntities = resolveHistory(allMessages, Long.valueOf(parentId));
        }

        List<Message> processedMessages = historyEntities.stream()
                .map(message -> {
                    MessageType type = MessageType.valueOf(message.getType());
                    return switch (type) {
                        case USER -> new UserMessage(message.getContent());
                        case ASSISTANT -> new AssistantMessage(message.getContent());
                        case SYSTEM -> new SystemMessage(message.getContent());
                        case TOOL -> ToolResponseMessage.builder().responses(List.of()).build();
                    };
                })
                .collect(Collectors.toList());

        processedMessages.addAll(chatClientRequest.prompt().getInstructions());

        ChatClientRequest processedRequest =
                chatClientRequest.mutate()
                        .prompt(chatClientRequest.prompt()
                                .mutate()
                                .messages(processedMessages)
                                .build())
                        .build();

        // 记录 USER 消息
        UserMessage userMessage = processedRequest.prompt().getUserMessage();

        org.mizoguchi.misaki.pojo.entity.Message userEntity =
                org.mizoguchi.misaki.pojo.entity.Message.builder()
                        .chatId(Long.valueOf(conversationId))
                        .parentId(parentId == null ? null : Long.valueOf(parentId))
                        .type(MessageType.USER.getValue())
                        .content(userMessage.getText())
                        .build();

        messageMapper.insert(userEntity);

        return processedRequest;
    }

    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {

        String conversationId = getConversationId(chatClientResponse.context());
        String parentId = getParentId(chatClientResponse.context());

        // conversationId == null → 不写聊天记忆
        if (conversationId == null) {
            return chatClientResponse;
        }

        if (chatClientResponse.chatResponse() != null) {
            AssistantMessage assistantMessage =
                    chatClientResponse.chatResponse()
                            .getResults()
                            .stream()
                            .map(Generation::getOutput)
                            .findFirst()
                            .orElse(null);

            if (assistantMessage != null) {
                org.mizoguchi.misaki.pojo.entity.Message assistantEntity =
                        org.mizoguchi.misaki.pojo.entity.Message.builder()
                                .chatId(Long.valueOf(conversationId))
                                .parentId(parentId == null ? null : Long.valueOf(parentId))
                                .type(MessageType.ASSISTANT.getValue())
                                .content(assistantMessage.getText())
                                .build();

                messageMapper.insert(assistantEntity);
            }
        }
        return chatClientResponse;
    }

    private String getConversationId(Map<String, Object> context) {
        return context == null ? null : Objects.toString(context.get(ChatConstant.CONVERSATION_ID), null);
    }

    private String getParentId(Map<String, Object> context) {
        return context == null ? null : Objects.toString(context.get(ChatConstant.PARENT_ID), null);
    }

    private List<org.mizoguchi.misaki.pojo.entity.Message> resolveHistory(List<org.mizoguchi.misaki.pojo.entity.Message> memoryMessages,
                                                                          Long parentId) {
        Map<Long, org.mizoguchi.misaki.pojo.entity.Message> messageMap =
                memoryMessages.stream()
                        .collect(Collectors.toMap(
                                org.mizoguchi.misaki.pojo.entity.Message::getId,
                                m -> m
                        ));

        List<org.mizoguchi.misaki.pojo.entity.Message> history = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        Long currentId = parentId;
        while (currentId != null) {
            if (!visited.add(currentId)) {
                break;
            }

            org.mizoguchi.misaki.pojo.entity.Message msg = messageMap.get(currentId);
            if (msg == null) {
                break;
            }

            history.add(msg);
            currentId = msg.getParentId();
        }

        Collections.reverse(history);
        return history;
    }

    public static Builder builder(MessageMapper messageMapper) {
        return new Builder(messageMapper);
    }

    public static final class Builder {
        private int order = -2147482648;
        private Scheduler scheduler;
        private final MessageMapper messageMapper;

        private Builder(MessageMapper messageMapper) {
            this.messageMapper = messageMapper;
            this.scheduler = BaseAdvisor.DEFAULT_SCHEDULER;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public Builder scheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public TreeMemoryAdvisor build() {
            return new TreeMemoryAdvisor(this.messageMapper, this.order, this.scheduler);
        }
    }
}
