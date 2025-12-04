package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.pojo.dto.front.AddAssistantFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateAssistantFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.AssistantFrontResponse;

import java.util.List;

public interface AssistantService {
    void addAssistant(Long userId, AddAssistantFrontRequest addAssistantFrontRequest);

    AssistantFrontResponse getAssistantFrontResponse(Long userId, Long assistantId);
    List<AssistantFrontResponse> listAssistantsFrontResponse(Long userId);
    List<AssistantFrontResponse> listPublicAssistantsFrontResponse(Long userId);

    void updateAssistant(Long userId, Long assistantId, UpdateAssistantFrontRequest updateAssistantFrontRequest);
    void deleteAssistant(Long userId, Long assistantId);
}
