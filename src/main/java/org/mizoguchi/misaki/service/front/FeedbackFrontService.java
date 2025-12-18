package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.dto.front.AddFeedbackFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.FeedbackFrontResponse;

import java.util.List;

public interface FeedbackFrontService {
    void addFeedback(Long userId, AddFeedbackFrontRequest addFeedbackFrontRequest);
    List<FeedbackFrontResponse> listFeedbacks(Long userId);
    void deleteFeedback(Long userId, Long feedbackId);
}
