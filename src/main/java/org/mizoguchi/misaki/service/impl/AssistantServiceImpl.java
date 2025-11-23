package org.mizoguchi.misaki.service.impl;

import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.entity.Assistant;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.service.AssistantService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssistantServiceImpl implements AssistantService {
    private final AssistantMapper assistantMapper;

    @Override
    public Assistant getAssistant(Long userId, Long assistantId) {
        Assistant assistant = assistantMapper.selectAssistantById(assistantId);
        if (assistant.getOwnerId().equals(userId)) {
            return assistant;
        }
        return null;
    }

    @Override
    public List<Assistant> getAssistants(Long userId) {
        return assistantMapper.selectAssistantsByOwnerId(userId);
    }
}
