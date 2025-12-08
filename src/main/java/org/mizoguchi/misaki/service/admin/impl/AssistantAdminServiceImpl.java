package org.mizoguchi.misaki.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.AssistantNotExistsException;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.pojo.dto.admin.AddAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.vo.admin.AssistantAdminResponse;
import org.mizoguchi.misaki.service.admin.AssistantAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssistantAdminServiceImpl implements AssistantAdminService {
    private final AssistantMapper assistantMapper;

    @Override
    public void addAssistant(AddAssistantAdminRequest addAssistantAdminRequest) {
        Assistant assistant = new Assistant();
        BeanUtils.copyProperties(addAssistantAdminRequest, assistant);
        assistantMapper.insert(assistant);
    }

    @Override
    public List<AssistantAdminResponse> listAssistants(Integer pageIndex, Integer pageSize) {
        Page<Assistant> page = new Page<>(pageIndex, pageSize);
        return assistantMapper.selectList(page, new LambdaQueryWrapper<>()).stream()
                .map(assistant -> {
                    AssistantAdminResponse assistantAdminResponse = new AssistantAdminResponse();
                    BeanUtils.copyProperties(assistant, assistantAdminResponse);

                    return assistantAdminResponse;
                }).collect(Collectors.toList());
    }

    @Override
    public List<AssistantAdminResponse> searchAssistants(SearchAssistantAdminRequest searchAssistantAdminRequest) {
        // TODO
        return List.of();
    }

    @Override
    public void updateAssistant(Long assistantId, UpdateAssistantAdminRequest updateAssistantAdminRequest) {
        Assistant assistant = new Assistant();
        BeanUtils.copyProperties(updateAssistantAdminRequest, assistant);
        int affectedRows = assistantMapper.update(assistant, new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getId, assistantId));

        if (affectedRows == 0) {
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }
    }

    @Override
    public void deleteAssistant(Long assistantId) {
        int affectedRows = assistantMapper.deleteById(assistantId);

        if (affectedRows == 0) {
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }
    }
}
