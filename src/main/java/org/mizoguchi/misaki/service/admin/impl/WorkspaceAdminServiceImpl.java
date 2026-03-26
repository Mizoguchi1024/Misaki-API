package org.mizoguchi.misaki.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.modelcontextprotocol.client.McpSyncClient;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.mapper.ChatMapper;
import org.mizoguchi.misaki.mapper.FeedbackMapper;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.pojo.dto.admin.AiBalanceResponse;
import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.entity.Chat;
import org.mizoguchi.misaki.pojo.entity.Feedback;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.vo.admin.WorkspaceAdminResponse;
import org.mizoguchi.misaki.service.admin.WorkspaceAdminService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceAdminServiceImpl implements WorkspaceAdminService {
    private static final int PROCESSING_FEEDBACK_STATUS = 0;

    @Value("${misaki.ai.api-key}")
    private String apiKey;

    private WebClient.Builder webClientBuilder;

    private final UserMapper userMapper;
    private final AssistantMapper assistantMapper;
    private final ChatMapper chatMapper;
    private final FeedbackMapper feedbackMapper;
    private final List<McpSyncClient> mcpSyncClients;

    @Override
    public WorkspaceAdminResponse getData(String range) {
        LocalDateTime startTime = getStartTime(range);
        int totalServers = mcpSyncClients.size();
        int totalTools = mcpSyncClients.stream()
                .mapToInt(mcpSyncClient -> mcpSyncClient.listTools().tools().size())
                .sum();

        AiBalanceResponse aiBalanceResponse = webClientBuilder.build()
                .get()
                .uri("https://api.deepseek.com/user/balance")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .bodyToMono(AiBalanceResponse.class)
                .block();

        return WorkspaceAdminResponse.builder()
                .mcpStats(WorkspaceAdminResponse.McpStats.builder()
                        .totalServers(totalServers)
                        .totalTools(totalTools)
                        .build())
                .userStats(WorkspaceAdminResponse.UserStats.builder()
                        .totalUsers(toInt(userMapper.selectCount(new LambdaQueryWrapper<User>()
                                .eq(User::getDeleteFlag, false))))
                        .newUsers(toInt(userMapper.selectCount(new LambdaQueryWrapper<User>()
                                .eq(User::getDeleteFlag, false)
                                .ge(User::getCreateTime, startTime))))
                        .build())
                .assistantStats(WorkspaceAdminResponse.AssistantStats.builder()
                        .totalAssistants(toInt(assistantMapper.selectCount(new LambdaQueryWrapper<Assistant>()
                                .eq(Assistant::getDeleteFlag, false))))
                        .newAssistants(toInt(assistantMapper.selectCount(new LambdaQueryWrapper<Assistant>()
                                .eq(Assistant::getDeleteFlag, false)
                                .ge(Assistant::getCreateTime, startTime))))
                        .totalPublicAssistants(toInt(assistantMapper.selectCount(new LambdaQueryWrapper<Assistant>()
                                .eq(Assistant::getDeleteFlag, false)
                                .eq(Assistant::getPublicFlag, true))))
                        .build())
                .chatStats(WorkspaceAdminResponse.ChatStats.builder()
                        .totalChats(toInt(chatMapper.selectCount(new LambdaQueryWrapper<Chat>()
                                .eq(Chat::getDeleteFlag, false))))
                        .newChats(toInt(chatMapper.selectCount(new LambdaQueryWrapper<Chat>()
                                .eq(Chat::getDeleteFlag, false)
                                .ge(Chat::getCreateTime, startTime))))
                        .build())
                .feedbackStats(WorkspaceAdminResponse.FeedbackStats.builder()
                        .newFeedbacks(toInt(feedbackMapper.selectCount(new LambdaQueryWrapper<Feedback>()
                                .eq(Feedback::getDeleteFlag, false)
                                .ge(Feedback::getCreateTime, startTime))))
                        .processingFeedbacks(toInt(feedbackMapper.selectCount(new LambdaQueryWrapper<Feedback>()
                                .eq(Feedback::getDeleteFlag, false)
                                .eq(Feedback::getStatus, PROCESSING_FEEDBACK_STATUS))))
                        .build())
                .aiBalance(WorkspaceAdminResponse.AiBalance.builder().isAvailable(aiBalanceResponse.isAvailable())
                        .balance(aiBalanceResponse.getBalanceInfos().getFirst().getTotalBalance())
                        .build())
                .build();
    }

    private LocalDateTime getStartTime(String range) {
        LocalDate today = LocalDate.now();

        return switch (range) {
            case "day" -> today.atStartOfDay();
            case "week" -> today.with(DayOfWeek.MONDAY).atStartOfDay();
            case "month" -> today.withDayOfMonth(1).atStartOfDay();
            default -> throw new IllegalArgumentException("Invalid parameter");
        };
    }

    private Integer toInt(Long count) {
        return Math.toIntExact(count);
    }
}
