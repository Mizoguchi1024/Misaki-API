package org.mizoguchi.misaki.pojo.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssistantStatsAdminResponse {
    private Integer totalAssistants;
    private Integer newAssistants;
    private Integer totalPublicAssistants;
}
