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
    private McpStatsAdminResponse mcpStats;

    private UserStatsAdminResponse userStats;

    private AssistantStatsAdminResponse assistantStats;

    private ChatStatsAdminResponse chatStats;
    
    private FeedbackStatsAdminResponse feedbackStats;
}
