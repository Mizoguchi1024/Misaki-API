package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.enumeration.LikesTargetTypeEnum;
import org.mizoguchi.misaki.common.exception.AssistantNotExistsException;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.mapper.LikesMapper;
import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.entity.Likes;
import org.mizoguchi.misaki.pojo.vo.front.AboutFrontResponse;
import org.mizoguchi.misaki.service.front.LikesFrontService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesFrontServiceImpl implements LikesFrontService {
    private final LikesMapper likesMapper;
    private final AssistantMapper assistantMapper;

    @Override
    public void likeMisaki(Long userId) {
        Likes existingLikes = likesMapper.selectOne(new LambdaQueryWrapper<Likes>()
                .eq(Likes::getUserId, userId)
                .eq(Likes::getTargetType, LikesTargetTypeEnum.MISAKI.getValue())
        );

        if (existingLikes == null) {
            Likes likes = Likes.builder()
                    .userId(userId)
                    .targetType(LikesTargetTypeEnum.MISAKI.getValue())
                    .build();

            likesMapper.insert(likes);
        }else {
            likesMapper.deleteById(existingLikes);
        }
    }

    @Override
    public void likeAssistant(Long userId, Long assistantId) {
        Assistant assistant = assistantMapper.selectOne(new LambdaQueryWrapper<Assistant>()
                .eq(Assistant::getId, assistantId)
                .eq(Assistant::getOwnerId, userId)
                .or()
                .eq(Assistant::getId, assistantId)
                .eq(Assistant::getPublicFlag, true)
                .eq(Assistant::getDeleteFlag, false)
        );

        if (assistant == null) {
            throw  new AssistantNotExistsException(FailMessageConstant.ASSISTANT_NOT_EXISTS);
        }

        Likes existingLikes = likesMapper.selectOne(new LambdaQueryWrapper<Likes>()
                .eq(Likes::getUserId, userId)
                .eq(Likes::getTargetType, LikesTargetTypeEnum.ASSISTANT.getValue())
                .eq(Likes::getTargetId, assistantId)
        );

        if (existingLikes == null) {
            Likes likes = Likes.builder()
                    .userId(userId)
                    .targetType(LikesTargetTypeEnum.ASSISTANT.getValue())
                    .targetId(assistantId)
                    .build();

            likesMapper.insert(likes);
        }else {
            likesMapper.deleteById(existingLikes);
        }
    }

    @Override
    public void likeScript(Long userId, Long scriptId) {

    }

    @Override
    public AboutFrontResponse getMisakiLikes(Long userId) {
        Long likesCount = likesMapper.selectCount(new LambdaQueryWrapper<Likes>()
                .eq(Likes::getTargetType, LikesTargetTypeEnum.MISAKI.getValue())
        );

        boolean likedFlag = likesMapper.exists(new LambdaQueryWrapper<Likes>()
                .eq(Likes::getUserId, userId)
                .eq(Likes::getTargetType, LikesTargetTypeEnum.MISAKI.getValue())
        );

        return AboutFrontResponse.builder()
                .likes(Math.toIntExact(likesCount))
                .likedFlag(likedFlag)
                .build();
    }
}
