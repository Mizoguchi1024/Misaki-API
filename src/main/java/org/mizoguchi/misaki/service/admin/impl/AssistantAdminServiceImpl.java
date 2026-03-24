package org.mizoguchi.misaki.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.SqlConstant;
import org.mizoguchi.misaki.common.exception.AssistantNotExistsException;
import org.mizoguchi.misaki.common.exception.OptimisticLockFailedException;
import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.pojo.dto.admin.AddAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.vo.admin.AssistantAdminResponse;
import org.mizoguchi.misaki.service.admin.AssistantAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
    public PageResult<AssistantAdminResponse> searchAssistants(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchAssistantAdminRequest searchAssistantAdminRequest) {
        IPage<Assistant> assistantsPage = assistantMapper.selectPage(new Page<>(pageIndex, pageSize), new QueryWrapper<Assistant>()
                .orderBy(sortField != null, sortOrder.equalsIgnoreCase(SqlConstant.ASC), sortField)
                .lambda()
                .like(searchAssistantAdminRequest.getId() != null, Assistant::getId, searchAssistantAdminRequest.getId())
                .like(searchAssistantAdminRequest.getName() != null, Assistant::getName, searchAssistantAdminRequest.getName())
                .like(searchAssistantAdminRequest.getPersonality() != null, Assistant::getPersonality, searchAssistantAdminRequest.getPersonality())
                .like(searchAssistantAdminRequest.getDetail() != null, Assistant::getDetail, searchAssistantAdminRequest.getDetail())
                .eq(searchAssistantAdminRequest.getGender() != null, Assistant::getGender, searchAssistantAdminRequest.getGender())
                .eq(searchAssistantAdminRequest.getBirthday() != null, Assistant::getBirthday, searchAssistantAdminRequest.getBirthday())
                .like(searchAssistantAdminRequest.getModelId() != null, Assistant::getModelId, searchAssistantAdminRequest.getModelId())
                .like(searchAssistantAdminRequest.getCreatorId() != null, Assistant::getCreatorId, searchAssistantAdminRequest.getCreatorId())
                .like(searchAssistantAdminRequest.getOwnerId() != null, Assistant::getOwnerId, searchAssistantAdminRequest.getOwnerId())
                .eq(searchAssistantAdminRequest.getPublicFlag() != null, Assistant::getPublicFlag, searchAssistantAdminRequest.getPublicFlag())
                .eq(searchAssistantAdminRequest.getDeleteFlag() != null, Assistant::getDeleteFlag, searchAssistantAdminRequest.getDeleteFlag())
                .like(searchAssistantAdminRequest.getCreateTime() != null, Assistant::getCreateTime, searchAssistantAdminRequest.getCreateTime())
                .like(searchAssistantAdminRequest.getUpdateTime() != null, Assistant::getUpdateTime, searchAssistantAdminRequest.getUpdateTime())
        );

        PageResult<AssistantAdminResponse> pageResult = new PageResult<>();
        pageResult.setList(assistantsPage.getRecords().stream()
                .map(assistant -> {
                    AssistantAdminResponse assistantAdminResponse = new AssistantAdminResponse();
                    BeanUtils.copyProperties(assistant, assistantAdminResponse);

                    return assistantAdminResponse;
                }).collect(Collectors.toList()));

        pageResult.setTotal(Math.toIntExact(assistantsPage.getTotal()));
        pageResult.setPageIndex(Math.toIntExact(assistantsPage.getCurrent()));
        pageResult.setPageSize(Math.toIntExact(assistantsPage.getSize()));

        return pageResult;
    }

    @Override
    public void updateAssistant(Long assistantId, UpdateAssistantAdminRequest updateAssistantAdminRequest) {
        boolean existsFlag = assistantMapper.exists(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getId, assistantId)
        );

        if (!existsFlag) {
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        Assistant assistant = new Assistant();
        BeanUtils.copyProperties(updateAssistantAdminRequest, assistant);
        assistant.setId(assistantId);
        int affectedRows = assistantMapper.updateById(assistant);

        if (affectedRows == 0) {
            throw new OptimisticLockFailedException(FailMessageConstant.OPTIMISTIC_LOCK_FAILED);
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
