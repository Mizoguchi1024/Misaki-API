package org.mizoguchi.misaki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.AssistantNotExistsException;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.mapper.LikeMapper;
import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.entity.Like;
import org.mizoguchi.misaki.service.LikeService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeMapper likeMapper;
    private final AssistantMapper assistantMapper;

    @Override
    public void likeMisaki(Long userId) {

    }

    @Override
    public void likeAssistant(Long userId, Long assistantId) {
        Assistant assistant = assistantMapper.selectOne(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getId, assistantId)
                .ne(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false));

        if (assistant == null) {
            throw  new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        Like existingLike = likeMapper.selectOne(new LambdaQueryWrapper<Like>()
                .eq(Like::getUserId, userId)
                .eq(Like::getTargetType, 1)
                .eq(Like::getTargetId, assistantId));

        if (existingLike == null) {
            Like like = Like.builder()
                    .userId(userId)
                    .targetType(1)
                    .targetId(assistantId)
                    .build();

            likeMapper.insert(like);
        }else {
            likeMapper.deleteById(existingLike);
        }
    }

    @Override
    public void likeScript(Long userId, Long scriptId) {

    }
}
