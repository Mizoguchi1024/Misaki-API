package org.mizoguchi.misaki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.AssistantNotExistsException;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.mapper.LikesMapper;
import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.entity.Likes;
import org.mizoguchi.misaki.service.LikesService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {
    private final LikesMapper likesMapper;
    private final AssistantMapper assistantMapper;

    @Override
    public void likesAssistant(Long userId, Long assistantId) {
        Assistant assistant = assistantMapper.selectOne(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getId, assistantId)
                .ne(Assistant::getOwnerId, userId)
                .eq(Assistant::getDeleteFlag, false));

        if (assistant == null) {
            throw  new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        Likes existingLikes = likesMapper.selectOne(new LambdaQueryWrapper<Likes>()
                .eq(Likes::getUserId, userId)
                .eq(Likes::getTargetType, 0)
                .eq(Likes::getTargetId, assistantId));

        if (existingLikes == null) {
            Likes likes = Likes.builder()
                    .userId(userId)
                    .targetType(0)
                    .targetId(assistantId)
                    .build();

            likesMapper.insert(likes);
        }else {
            likesMapper.deleteById(existingLikes);
        }
    }
}
