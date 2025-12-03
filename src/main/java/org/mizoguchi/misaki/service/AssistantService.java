package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.dto.front.AddAssistantFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateAssistantFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.AssistantFrontResponse;

import java.util.List;

public interface AssistantService {
    Assistant getAssistantEntity(Long userId, Long assistantId);
    List<Assistant> listAssistantsEntity(Long userId);
    void addAssistant(Long userId, AddAssistantFrontRequest addAssistantFrontRequest);
    void updateAssistant(Long userId, Long assistantId, UpdateAssistantFrontRequest updateAssistantFrontRequest);
    void deleteAssistant(Long userId, Long assistantId);

    AssistantFrontResponse getAssistantFrontResponse(Long userId, Long assistantId);
    List<AssistantFrontResponse> listAssistantsFrontResponse(Long userId);
    List<AssistantFrontResponse> listPublicAssistantsFrontResponse(Long userId);

}
