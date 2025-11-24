package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Assistant;
import org.mizoguchi.misaki.entity.dto.CreateAssistantRequest;
import org.mizoguchi.misaki.entity.dto.EditAssistantRequest;
import org.mizoguchi.misaki.entity.vo.UserAssistantResponse;

import java.util.List;

public interface AssistantService {
    Assistant getAssistantEntity(Long userId, Long assistantId);
    UserAssistantResponse getAssistant(Long userId, Long assistantId);
    List<UserAssistantResponse> getAssistants(Long userId);
    List<UserAssistantResponse> getPublicAssistants(Long userId);
    void createAssistant(Long userId, CreateAssistantRequest createAssistantRequest);
    void editAssistant(EditAssistantRequest editAssistantRequest);
}
