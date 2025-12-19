package org.mizoguchi.misaki.advisor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Getter;
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
    private final String defaultConversationId;
    private final String defaultParentId;
    private final int order;
    private final Scheduler scheduler;

    private TreeMemoryAdvisor(MessageMapper messageMapper, String defaultConversationId, String defaultParentId, int order, Scheduler scheduler) {
        Assert.notNull(messageMapper, "messageMapper cannot be null");
        Assert.hasText(defaultConversationId, "defaultConversationId cannot be null or empty");
        Assert.hasText(defaultParentId, "defaultParentId cannot be null or empty");
        Assert.notNull(scheduler, "scheduler cannot be null");
        this.messageMapper = messageMapper;
        this.defaultConversationId = defaultConversationId;
        this.defaultParentId = defaultParentId;
        this.order = order;
        this.scheduler = scheduler;
    }

    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String conversationId = this.getConversationId(chatClientRequest.context(), this.defaultConversationId);
        String parentId = this.getParentId(chatClientRequest.context(), defaultParentId);
        List<org.mizoguchi.misaki.pojo.entity.Message> memoryMessages = this.resolveHistory(this.messageMapper.selectList(new LambdaQueryWrapper<org.mizoguchi.misaki.pojo.entity.Message>().eq(org.mizoguchi.misaki.pojo.entity.Message::getChatId, conversationId)), Long.valueOf(parentId));
        List<Message> processedMessages = memoryMessages.stream().map(message -> {
            String content = message.getContent();
            MessageType type = MessageType.valueOf(message.getType());
            Message var;
            switch (type) {
                case USER -> var = new UserMessage(content);
                case ASSISTANT -> var = new AssistantMessage(content);
                case SYSTEM -> var = new SystemMessage(content);
                case TOOL -> var = ToolResponseMessage.builder().responses(List.of()).build();
                default -> throw new IncompatibleClassChangeError();
            }
            return var;
        }).collect(Collectors.toList());
        processedMessages.addAll(chatClientRequest.prompt().getInstructions());
        ChatClientRequest processedChatClientRequest = chatClientRequest.mutate().prompt(chatClientRequest.prompt().mutate().messages(processedMessages).build()).build();
        UserMessage userMessage = processedChatClientRequest.prompt().getUserMessage();

        org.mizoguchi.misaki.pojo.entity.Message processedUserMessage = org.mizoguchi.misaki.pojo.entity.Message.builder()
                .chatId(Long.valueOf(conversationId))
                .parentId(Long.valueOf(parentId))
                .type(MessageType.USER.getValue())
                .content(userMessage.getText())
                .build();

        this.messageMapper.insert(processedUserMessage);
        return processedChatClientRequest;
    }

    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        if (chatClientResponse.chatResponse() != null) {
            AssistantMessage assistantMessage =
                    chatClientResponse.chatResponse()
                            .getResults()
                            .stream()
                            .map(Generation::getOutput)
                            .findFirst()
                            .orElse(null);

            if (assistantMessage != null) {
                org.mizoguchi.misaki.pojo.entity.Message processedAssistantMessage = org.mizoguchi.misaki.pojo.entity.Message.builder()
                        .chatId(Long.valueOf(this.getConversationId(chatClientResponse.context(), this.defaultConversationId)))
                        .parentId(Long.valueOf(this.getParentId(chatClientResponse.context(), defaultParentId)))
                        .type(MessageType.ASSISTANT.getValue())
                        .content(assistantMessage.getText())
                        .build();

                messageMapper.insert(processedAssistantMessage);
            }
        }
        return chatClientResponse;
    }

    private String getParentId(Map<String, Object> context, String defaultParentId) {
        Assert.notNull(context, "context cannot be null");
        Assert.noNullElements(context.keySet().toArray(), "context cannot contain null keys");
        Assert.hasText(defaultParentId, "defaultParentId cannot be null or empty");
        return context.containsKey("chat_memory_parent_id") ? context.get("chat_memory_parent_id").toString() : defaultParentId;
    }

    private List<org.mizoguchi.misaki.pojo.entity.Message> resolveHistory(List<org.mizoguchi.misaki.pojo.entity.Message> memoryMessages, Long parentId) {
        // 1. 建索引：id -> message
        Map<Long, org.mizoguchi.misaki.pojo.entity.Message> messageMap = memoryMessages.stream()
                .collect(Collectors.toMap(
                        org.mizoguchi.misaki.pojo.entity.Message::getId,
                        m -> m
                ));

        List<org.mizoguchi.misaki.pojo.entity.Message> history = new ArrayList<>();
        Set<Long> visited = new HashSet<>(); // 防脏数据死循环

        Long currentId = parentId;
        while (currentId != null) {
            if (!visited.add(currentId)) {
                break; // parentId 循环引用
            }

            org.mizoguchi.misaki.pojo.entity.Message msg = messageMap.get(currentId);
            if (msg == null) {
                break; // 链断了
            }

            history.add(msg);
            currentId = msg.getParentId();
        }

        // 当前顺序：新 → 旧
        Collections.reverse(history);
        return history;
    }


    public static TreeMemoryAdvisor.Builder builder(MessageMapper messageMapper) {
        return new TreeMemoryAdvisor.Builder(messageMapper);
    }

    public static final class Builder {
        private String conversationId = "default";
        private String parentId = "default";
        private int order = -2147482648;
        private Scheduler scheduler;
        private MessageMapper messageMapper;

        private Builder(MessageMapper messageMapper) {
            this.scheduler = BaseAdvisor.DEFAULT_SCHEDULER;
            this.messageMapper = messageMapper;
        }

        public TreeMemoryAdvisor.Builder conversationId(String conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public TreeMemoryAdvisor.Builder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public TreeMemoryAdvisor.Builder order(int order) {
            this.order = order;
            return this;
        }

        public TreeMemoryAdvisor.Builder scheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public TreeMemoryAdvisor build() {
            return new TreeMemoryAdvisor(this.messageMapper, this.conversationId, this.parentId, this.order, this.scheduler);
        }
    }
}
