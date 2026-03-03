package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.pojo.dto.front.AddAssistantFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateAssistantFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.AssistantFrontResponse;

import java.util.List;

public interface AssistantFrontService {
    void addAssistant(Long userId, AddAssistantFrontRequest addAssistantFrontRequest);
    void copyAssistant(Long userId, Long assistantId);
    AssistantFrontResponse getAssistant(Long userId, Long assistantId);
    List<AssistantFrontResponse> listAssistants(Long userId);
    PageResult<AssistantFrontResponse> listPublicAssistants(Long userId, Integer pageIndex, Integer pageSize);
    void updateAssistant(Long userId, Long assistantId, UpdateAssistantFrontRequest updateAssistantFrontRequest);
    void deleteAssistant(Long userId, Long assistantId);
}
