package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.FeedbackNotExistsException;
import org.mizoguchi.misaki.mapper.FeedbackMapper;
import org.mizoguchi.misaki.pojo.dto.front.AddFeedbackFrontRequest;
import org.mizoguchi.misaki.pojo.entity.Feedback;
import org.mizoguchi.misaki.pojo.vo.front.FeedbackFrontResponse;
import org.mizoguchi.misaki.service.front.FeedbackFrontService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackFrontServiceImpl implements FeedbackFrontService {
    private final FeedbackMapper feedbackMapper;

    @Override
    public void addFeedback(Long userId, AddFeedbackFrontRequest addFeedbackFrontRequest) {
        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(addFeedbackFrontRequest, feedback);
        feedback.setUserId(userId);
        feedbackMapper.insert(feedback);
    }

    @Override
    public List<FeedbackFrontResponse> listFeedbacks(Long userId) {
        List<Feedback> feedbacks = feedbackMapper.selectList(new LambdaQueryWrapper<Feedback>()
                .eq(Feedback::getUserId, userId)
                .eq(Feedback::getDeleteFlag, false));

        return feedbacks.stream().map(feedback -> {
            FeedbackFrontResponse feedbackFrontResponse = new FeedbackFrontResponse();
            BeanUtils.copyProperties(feedback, feedbackFrontResponse);
            return feedbackFrontResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteFeedback(Long userId, Long feedbackId) {
        int affectedRows = feedbackMapper.update(new LambdaUpdateWrapper<Feedback>()
                .eq(Feedback::getId, feedbackId)
                .eq(Feedback::getUserId, userId)
                .eq(Feedback::getDeleteFlag, false)
                .set(Feedback::getDeleteFlag, true));

        if(affectedRows == 0) {
            throw new FeedbackNotExistsException(FailMessageConstant.FEEDBACK_NOT_EXISTS);
        }
    }
}
