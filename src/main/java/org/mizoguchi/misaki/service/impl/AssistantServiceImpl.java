package org.mizoguchi.misaki.service.impl;

import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.entity.Assistant;
import org.mizoguchi.misaki.entity.dto.CreateAssistantRequest;
import org.mizoguchi.misaki.entity.dto.EditAssistantRequest;
import org.mizoguchi.misaki.entity.vo.UserAssistantResponse;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.service.AssistantService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssistantServiceImpl implements AssistantService {
    private final AssistantMapper assistantMapper;

    @Override
    public Assistant getAssistantEntity(Long userId, Long assistantId) {
        Assistant assistant = assistantMapper.selectAssistantById(assistantId);

        if (assistant.getOwnerId().equals(userId)) {
            return assistant;
        }

        return null;
    }

    @Override
    public UserAssistantResponse getAssistant(Long userId, Long assistantId) {
        Assistant assistant = getAssistantEntity(userId, assistantId);
        UserAssistantResponse userAssistantResponse = new UserAssistantResponse();
        BeanUtils.copyProperties(assistant, userAssistantResponse);

        return userAssistantResponse;
    }

    @Override
    public List<UserAssistantResponse> getAssistants(Long userId) {
//        return assistantMapper.selectAssistantsByOwnerId(userId); TODO
        return List.of();
    }

    @Override
    public List<UserAssistantResponse> getPublicAssistants(Long userId) {
        return List.of();
    }

    @Override
    public void createAssistant(Long userId, CreateAssistantRequest createAssistantRequest) {

    }

    @Override
    public void editAssistant(EditAssistantRequest editAssistantRequest) {

    }
}
