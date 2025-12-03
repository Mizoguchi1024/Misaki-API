package org.mizoguchi.misaki.service.impl;

import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.dto.front.AddAssistantFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateAssistantFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.AssistantFrontResponse;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.service.AssistantService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<Assistant> listAssistantsEntity(Long userId) {
        return assistantMapper.selectAssistantsByOwnerId(userId);
    }

    @Override
    public AssistantFrontResponse getAssistantFrontResponse(Long userId, Long assistantId) {
        Assistant assistant = getAssistantEntity(userId, assistantId);
        AssistantFrontResponse assistantFrontResponse = new AssistantFrontResponse();
        BeanUtils.copyProperties(assistant, assistantFrontResponse);

        return assistantFrontResponse;
    }

    @Override
    public List<AssistantFrontResponse> listAssistantsFrontResponse(Long userId) {
        List<AssistantFrontResponse> assistants = listAssistantsEntity(userId).stream()
                .map(assistant -> {
                    AssistantFrontResponse assistantFrontResponse = new AssistantFrontResponse();
                    BeanUtils.copyProperties(assistant, assistantFrontResponse);
                    return assistantFrontResponse;
                }).collect(Collectors.toList());

        return assistants;
    }

    @Override
    public List<AssistantFrontResponse> listPublicAssistantsFrontResponse(Long userId) {
        return List.of();
    }

    @Override
    public void addAssistant(Long userId, AddAssistantFrontRequest addAssistantFrontRequest) {
        // TODO 限制最大存档数量
        Assistant assistant = new Assistant();
        BeanUtils.copyProperties(addAssistantFrontRequest, assistant);
        assistant.setOwnerId(userId);
        assistantMapper.insertAssistant(assistant);
    }

    @Override
    public void updateAssistant(Long userId, Long assistantId, UpdateAssistantFrontRequest updateAssistantFrontRequest) {

    }

    @Override
    public void deleteAssistant(Long userId, Long assistantId) {

    }
}
