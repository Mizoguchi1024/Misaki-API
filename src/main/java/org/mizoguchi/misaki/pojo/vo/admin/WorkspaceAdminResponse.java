package org.mizoguchi.misaki.pojo.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceAdminResponse {
    private McpStats mcpStats;

    private UserStats userStats;

    private AssistantStats assistantStats;

    private ChatStats chatStats;

    private FeedbackStats feedbackStats;

    private AiBalance aiBalance;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class McpStats {
        private Integer totalServers;

        private Integer totalTools;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private Integer totalUsers;

        private Integer newUsers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssistantStats {
        private Integer totalAssistants;

        private Integer newAssistants;

        private Integer totalPublicAssistants;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatStats {
        private Integer totalChats;

        private Integer newChats;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackStats {
        private Integer newFeedbacks;

        private Integer processingFeedbacks;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiBalance {
        private Boolean isAvailable;

        private String balance;

        private String currency;
    }
}
