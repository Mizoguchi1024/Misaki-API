package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Assistant;

import java.util.List;

public interface AssistantService {
    Assistant getAssistant(Long userId, Long assistantId);
    List<Assistant> getAssistants(Long userId);
}
