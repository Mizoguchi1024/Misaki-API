package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.enumeration.LikesTargetTypeEnum;
import org.mizoguchi.misaki.common.exception.*;
import org.mizoguchi.misaki.mapper.LikesMapper;
import org.mizoguchi.misaki.mapper.ModelUserMapper;
import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.dto.front.AddAssistantFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateAssistantFrontRequest;
import org.mizoguchi.misaki.pojo.entity.Likes;
import org.mizoguchi.misaki.pojo.entity.ModelUser;
import org.mizoguchi.misaki.pojo.vo.front.AssistantFrontResponse;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.service.front.AssistantFrontService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssistantFrontServiceImpl implements AssistantFrontService {
    private final AssistantMapper assistantMapper;
    private final LikesMapper likesMapper;
    private final ModelUserMapper modelUserMapper;

    @Override
    public void addAssistant(Long userId, AddAssistantFrontRequest addAssistantFrontRequest) {
        Long existingAssistantCount = assistantMapper.selectCount(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false));

        if (existingAssistantCount > 20) {
            throw new TooManyAssistantsException(FailMessageConstant.TOO_MANY_ASSISTANTS);
        }

        ModelUser modelUser = modelUserMapper.selectOne(new LambdaQueryWrapper<ModelUser>()
                .eq(ModelUser::getUserId, userId)
                .eq(ModelUser::getModelId, addAssistantFrontRequest.getModelId())
        );

        if (modelUser == null) {
            throw new ModelNotOwnedException(FailMessageConstant.MODEL_NOT_OWNED);
        }

        Assistant assistant = new Assistant();
        BeanUtils.copyProperties(addAssistantFrontRequest, assistant);
        assistant.setCreatorId(userId);
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

        ModelUser modelUser = modelUserMapper.selectOne(new LambdaQueryWrapper<ModelUser>()
                .eq(ModelUser::getUserId, userId)
                .eq(ModelUser::getModelId, assistant.getModelId())
        );

        if (modelUser == null) {
            throw new ModelNotOwnedException(FailMessageConstant.MODEL_NOT_OWNED);
        }

        Assistant assistantCopy = new Assistant();
        BeanUtils.copyProperties(assistant, assistantCopy);
        assistantCopy.setId(null);
        assistantCopy.setOwnerId(userId);
        assistantCopy.setPublicFlag(false);
        assistantCopy.setCreateTime(null);
        assistantCopy.setUpdateTime(null);

        assistantMapper.insert(assistantCopy);
    }

    @Override
    public AssistantFrontResponse getAssistant(Long userId, Long assistantId) {
        Assistant assistant = assistantMapper.selectOne(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getId, assistantId)
                .eq(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false));

        if (assistant == null) {
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        AssistantFrontResponse assistantFrontResponse = new AssistantFrontResponse();
        BeanUtils.copyProperties(assistant, assistantFrontResponse);

        Long likesCount = likesMapper.selectCount(new LambdaQueryWrapper<Likes>()
                .eq(Likes::getTargetType, LikesTargetTypeEnum.ASSISTANT.getValue())
                .eq(Likes::getTargetId, assistant.getId())
        );
        assistantFrontResponse.setLikes(Math.toIntExact(likesCount));

        boolean likedFlag = likesMapper.exists(new LambdaQueryWrapper<Likes>()
                .eq(Likes::getUserId, userId)
                .eq(Likes::getTargetType, LikesTargetTypeEnum.ASSISTANT.getValue())
                .eq(Likes::getTargetId, assistant.getId())
        );
        assistantFrontResponse.setLikedFlag(likedFlag);

        Long duplicateNameCount = assistantMapper.selectCount(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getName, assistant.getName())
                .ne(Assistant::getId, assistant.getId())
                .eq(Assistant::getDeleteFlag, false));
        assistantFrontResponse.setDuplicateName(Math.toIntExact(duplicateNameCount));

        return assistantFrontResponse;
    }

    @Override
    public List<AssistantFrontResponse> listAssistants(Long userId) {
        List<Assistant> assistants = assistantMapper.selectList(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false)
        );

        return assistants.stream().map(assistant -> {
            AssistantFrontResponse assistantFrontResponse = new AssistantFrontResponse();
            BeanUtils.copyProperties(assistant, assistantFrontResponse);

            Long likesCount = likesMapper.selectCount(new LambdaQueryWrapper<Likes>()
                    .eq(Likes::getTargetType, LikesTargetTypeEnum.ASSISTANT.getValue())
                    .eq(Likes::getTargetId, assistant.getId())
            );
            assistantFrontResponse.setLikes(Math.toIntExact(likesCount));

            boolean likedFlag = likesMapper.exists(new LambdaQueryWrapper<Likes>()
                    .eq(Likes::getUserId, userId)
                    .eq(Likes::getTargetType, LikesTargetTypeEnum.ASSISTANT.getValue())
                    .eq(Likes::getTargetId, assistant.getId())
            );
            assistantFrontResponse.setLikedFlag(likedFlag);

            Long duplicateNameCount = assistantMapper.selectCount(new LambdaQueryWrapper<Assistant>()
                    .eq(Assistant::getName, assistant.getName())
                    .ne(Assistant::getId, assistant.getId())
                    .eq(Assistant::getDeleteFlag, false)
            );
            assistantFrontResponse.setDuplicateName(Math.toIntExact(duplicateNameCount));

            return assistantFrontResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AssistantFrontResponse> listPublicAssistants(Long userId, Integer pageIndex, Integer pageSize) {
        List<Assistant> assistants = assistantMapper.selectList(new Page<>(pageIndex, pageSize), new LambdaQueryWrapper<Assistant>()
                .ne(Assistant::getOwnerId, userId)
                .eq(Assistant::getPublicFlag, true)
                .eq(Assistant::getDeleteFlag, false)
        );

        return assistants.stream().map(assistant -> {
            AssistantFrontResponse assistantFrontResponse = new AssistantFrontResponse();
            BeanUtils.copyProperties(assistant, assistantFrontResponse);

            Long likesCount = likesMapper.selectCount(new LambdaQueryWrapper<Likes>()
                    .eq(Likes::getTargetType, LikesTargetTypeEnum.ASSISTANT.getValue())
                    .eq(Likes::getTargetId, assistant.getId()));
            assistantFrontResponse.setLikes(Math.toIntExact(likesCount));

            boolean likedFlag = likesMapper.exists(new LambdaQueryWrapper<Likes>()
                    .eq(Likes::getUserId, userId)
                    .eq(Likes::getTargetType, LikesTargetTypeEnum.ASSISTANT.getValue())
                    .eq(Likes::getTargetId, assistant.getId())
            );
            assistantFrontResponse.setLikedFlag(likedFlag);

            Long duplicateNameCount = assistantMapper.selectCount(new LambdaQueryWrapper<Assistant>()
                    .eq(Assistant::getName, assistant.getName())
                    .ne(Assistant::getId, assistant.getId())
                    .eq(Assistant::getDeleteFlag, false));
            assistantFrontResponse.setDuplicateName(Math.toIntExact(duplicateNameCount));

            return assistantFrontResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateAssistant(Long userId, Long assistantId, UpdateAssistantFrontRequest updateAssistantFrontRequest) {
        boolean existsFlag = assistantMapper.exists(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getId, assistantId)
                .eq(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false)
        );

        if(!existsFlag){
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        Assistant assistant= new Assistant();
        BeanUtils.copyProperties(updateAssistantFrontRequest, assistant);
        assistant.setId(assistantId);
        int affectedRows = assistantMapper.updateById(assistant);

        if (affectedRows == 0) {
            throw new OptimisticLockFailedException(FailMessageConstant.OPTIMISTIC_LOCK_FAILED);
        }
    }

    @Override
    public void deleteAssistant(Long userId, Long assistantId) {
        Long assistantCount = assistantMapper.selectCount(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false));

        if (assistantCount <= 1){
            throw new AtLeastOneAssistantException(FailMessageConstant.AT_LEAST_ONE_ASSISTANT);
        }

        int affectedRows = assistantMapper.update(new LambdaUpdateWrapper<Assistant>()
                .eq(Assistant::getId, assistantId)
                .eq(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false)
                .set(Assistant::getDeleteFlag, true)
                .setIncrBy(Assistant::getVersion, 1)
        );

        if (affectedRows == 0){
            throw new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }
    }
}
