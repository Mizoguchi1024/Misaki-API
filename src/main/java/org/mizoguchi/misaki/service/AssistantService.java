package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Assistant;
import org.mizoguchi.misaki.entity.dto.front.AddAssistantFrontRequest;
import org.mizoguchi.misaki.entity.dto.front.UpdateAssistantFrontRequest;
import org.mizoguchi.misaki.entity.vo.front.AssistantFrontResponse;

import java.util.List;

public interface AssistantService {
    Assistant getAssistantEntity(Long userId, Long assistantId);
    List<Assistant> listAssistantsEntity(Long userId);
    void addAssistant(Long userId, AddAssistantFrontRequest addAssistantFrontRequest);
    void updateAssistant(UpdateAssistantFrontRequest updateAssistantFrontRequest);

    AssistantFrontResponse getAssistantFrontResponse(Long userId, Long assistantId);
    List<AssistantFrontResponse> listAssistantsFrontResponse(Long userId);
    List<AssistantFrontResponse> listPublicAssistantsFrontResponse(Long userId);

}
