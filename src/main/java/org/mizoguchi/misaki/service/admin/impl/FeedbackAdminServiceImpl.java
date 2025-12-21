package org.mizoguchi.misaki.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.FeedbackNotExistsException;
import org.mizoguchi.misaki.mapper.FeedbackMapper;
import org.mizoguchi.misaki.pojo.dto.admin.SearchFeedbackAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateFeedbackAdminRequest;
import org.mizoguchi.misaki.pojo.entity.Feedback;
import org.mizoguchi.misaki.pojo.vo.admin.FeedbackAdminResponse;
import org.mizoguchi.misaki.service.admin.FeedbackAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackAdminServiceImpl implements FeedbackAdminService {
    private final FeedbackMapper feedbackMapper;

    @Override
    public List<FeedbackAdminResponse> searchFeedbacks(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchFeedbackAdminRequest searchFeedbackAdminRequest) {
        List<Feedback> feedbacks = feedbackMapper.selectList(new Page<>(pageIndex, pageSize), new QueryWrapper<Feedback>()
                .orderBy(sortField != null, sortOrder.equalsIgnoreCase("asc"), sortField)
                .lambda()
                .like(searchFeedbackAdminRequest.getId() != null, Feedback::getId, searchFeedbackAdminRequest.getId())
                .like(searchFeedbackAdminRequest.getUserId() != null, Feedback::getUserId, searchFeedbackAdminRequest.getUserId())
                .like(searchFeedbackAdminRequest.getReplierId() != null, Feedback::getReplierId, searchFeedbackAdminRequest.getReplierId())
                .eq(searchFeedbackAdminRequest.getType() != null, Feedback::getType, searchFeedbackAdminRequest.getType())
                .like(searchFeedbackAdminRequest.getTitle() != null, Feedback::getTitle, searchFeedbackAdminRequest.getTitle())
                .like(searchFeedbackAdminRequest.getContent() != null, Feedback::getContent, searchFeedbackAdminRequest.getContent())
                .like(searchFeedbackAdminRequest.getReply() != null, Feedback::getReply, searchFeedbackAdminRequest.getReply())
                .eq(searchFeedbackAdminRequest.getStatus() != null, Feedback::getStatus, searchFeedbackAdminRequest.getStatus())
                .eq(searchFeedbackAdminRequest.getDeleteFlag() != null, Feedback::getDeleteFlag, searchFeedbackAdminRequest.getDeleteFlag())
                .eq(searchFeedbackAdminRequest.getCreateTime() != null, Feedback::getCreateTime, searchFeedbackAdminRequest.getCreateTime())
                .eq(searchFeedbackAdminRequest.getUpdateTime() != null, Feedback::getUpdateTime, searchFeedbackAdminRequest.getUpdateTime())
        );

        return feedbacks.stream()
            .map(feedback -> {
                FeedbackAdminResponse feedbackAdminResponse = new FeedbackAdminResponse();
                BeanUtils.copyProperties(feedback, feedbackAdminResponse);
                
                return feedbackAdminResponse;
            }).collect(Collectors.toList());
    }

    @Override
    public void updateFeedback(Long feedbackId, UpdateFeedbackAdminRequest updateFeedbackAdminRequest) {
        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(updateFeedbackAdminRequest, feedback);
        int affectedRows = feedbackMapper.update(feedback, new LambdaUpdateWrapper<Feedback>().eq(Feedback::getId, feedbackId));

        if(affectedRows == 0) {
            throw new FeedbackNotExistsException(FailMessageConstant.FEEDBACK_NOT_EXISTS);
        }
    }

    @Override
    public void deleteFeedback(Long feedbackId) {
        int affectedRows = feedbackMapper.deleteById(feedbackId);

        if(affectedRows == 0) {
            throw new FeedbackNotExistsException(FailMessageConstant.FEEDBACK_NOT_EXISTS);
        }
    }
}
