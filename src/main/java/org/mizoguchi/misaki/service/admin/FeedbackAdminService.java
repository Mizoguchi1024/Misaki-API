package org.mizoguchi.misaki.service.admin;

import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.pojo.dto.admin.SearchFeedbackAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateFeedbackAdminRequest;
import org.mizoguchi.misaki.pojo.vo.admin.FeedbackAdminResponse;

public interface FeedbackAdminService {
    PageResult<FeedbackAdminResponse> searchFeedbacks(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchFeedbackAdminRequest searchFeedbackAdminRequest);
    void updateFeedback(Long feedbackId, UpdateFeedbackAdminRequest updateFeedbackAdminRequest);
    void deleteFeedback(Long feedbackId);
}
