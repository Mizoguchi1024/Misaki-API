package org.mizoguchi.misaki.pojo.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackStatsAdminResponse {
    private Integer newFeedbacks;
    
    private Integer processingFeedbacks;
}
