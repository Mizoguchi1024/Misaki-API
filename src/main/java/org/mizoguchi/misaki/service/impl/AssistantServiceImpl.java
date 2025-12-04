package org.mizoguchi.misaki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.AssistantNotExistsException;
import org.mizoguchi.misaki.common.exception.AtLeastOneAssistantException;
import org.mizoguchi.misaki.common.exception.TooManyAssistantsException;
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
    public AssistantFrontResponse getAssistantFrontResponse(Long userId, Long assistantId) {
        Assistant assistant = assistantMapper.selectOne(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getId, assistantId)
                .eq(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false));

        if (assistant == null) {
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        AssistantFrontResponse assistantFrontResponse = new AssistantFrontResponse();
        BeanUtils.copyProperties(assistant, assistantFrontResponse);

        return assistantFrontResponse;
    }

    @Override
    public List<AssistantFrontResponse> listAssistantsFrontResponse(Long userId) {
        List<Assistant> assistants = assistantMapper.selectList(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false));

        if (assistants.isEmpty()){
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        return assistants.stream().map(assistant -> {
            AssistantFrontResponse assistantFrontResponse = new AssistantFrontResponse();
            BeanUtils.copyProperties(assistant, assistantFrontResponse);
            return assistantFrontResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AssistantFrontResponse> listPublicAssistantsFrontResponse(Long userId,Integer pageIndex, Integer pageSize) {
        Page<Assistant> page = new Page<>(pageIndex, pageSize);
        List<Assistant> assistants = assistantMapper.selectList(page, new LambdaQueryWrapper<Assistant>()
                .ne(Assistant::getOwnerId, userId)
                .eq(Assistant::getPublicFlag, true)
                .eq(Assistant::getDeleteFlag, false));

        if (assistants.isEmpty()){
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        return assistants.stream().map(assistant -> {
            AssistantFrontResponse assistantFrontResponse = new AssistantFrontResponse();
            BeanUtils.copyProperties(assistant, assistantFrontResponse);
            return assistantFrontResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public void addAssistant(Long userId, AddAssistantFrontRequest addAssistantFrontRequest) {
        Long existingAssistantCount = assistantMapper.selectCount(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false));

        if (existingAssistantCount > 20) {
            throw new TooManyAssistantsException(FailMessageConstant.TOO_MANY_ASSISTANTS);
        }

        Assistant assistant = new Assistant();
        BeanUtils.copyProperties(addAssistantFrontRequest, assistant);
        assistant.setOwnerId(userId);
        assistantMapper.insert(assistant);
    }

    @Override
    public void copyAssistant(Long userId, Long assistantId) {
        Assistant assistant = assistantMapper.selectOne(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getId, assistantId)
                .ne(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false));

        if(assistant == null){
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        // TODO 检查用户是否拥有此模型

        Assistant assistantCopy = new Assistant();
        BeanUtils.copyProperties(assistant, assistantCopy);
        assistantCopy.setId(null);
        assistantCopy.setOwnerId(userId);
        assistantCopy.setMoe(0L);
        assistantCopy.setPublicFlag(false);
        assistantCopy.setCreateTime(null);
        assistantCopy.setUpdateTime(null);

        assistantMapper.insert(assistantCopy);
    }

    @Override
    public void updateAssistant(Long userId, Long assistantId, UpdateAssistantFrontRequest updateAssistantFrontRequest) {
        Assistant assistant = assistantMapper.selectOne(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getId, assistantId)
                .eq(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false));

        if(assistant == null){
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        BeanUtils.copyProperties(updateAssistantFrontRequest, assistant);

        assistantMapper.updateById(assistant);
    }

    @Override
    public void deleteAssistant(Long userId, Long assistantId) {
        Assistant assistant = assistantMapper.selectOne(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getId, assistantId)
                .eq(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false));

        if (assistant == null) {
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        Long assistantCount = assistantMapper.selectCount(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false));

        if (assistantCount <= 1){
            throw new AtLeastOneAssistantException(FailMessageConstant.AT_LEAST_ONE_ASSISTANT);
        }

        assistantMapper.update(new LambdaUpdateWrapper<Assistant>()
                .eq(Assistant::getId, assistantId)
                .set(Assistant::getDeleteFlag, true));
    }
}
