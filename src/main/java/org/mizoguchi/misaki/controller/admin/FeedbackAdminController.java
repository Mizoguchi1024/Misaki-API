package org.mizoguchi.misaki.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.InvalidSortParamsException;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.admin.SearchFeedbackAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateFeedbackAdminRequest;
import org.mizoguchi.misaki.pojo.entity.Feedback;
import org.mizoguchi.misaki.pojo.vo.admin.FeedbackAdminResponse;
import org.mizoguchi.misaki.service.admin.FeedbackAdminService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/feedbacks")
@RequiredArgsConstructor
@Tag(name = "管理端-反馈相关接口")
public class FeedbackAdminController {
    private final FeedbackAdminService feedbackAdminService;

    @Operation(summary = "分页条件搜索反馈")
    @PostMapping("/search")
    public Result<List<FeedbackAdminResponse>> searchFeedbacks(@RequestParam @Positive Integer pageIndex,
                                                       @RequestParam @Positive Integer pageSize,
                                                       @RequestParam(required = false) String sortField,
                                                       @RequestParam(defaultValue = "asc") String sortOrder,
                                                       @RequestBody @Validated SearchFeedbackAdminRequest searchFeedbackAdminRequest){
        if (sortField != null && !sortField.isBlank()){
            try {
                Feedback.class.getDeclaredField(sortField);
            } catch (NoSuchFieldException e) {
                throw new InvalidSortParamsException(FailMessageConstant.INVALID_SORT_PARAMS);
            }
        }
        return Result.success(feedbackAdminService.searchFeedbacks(pageIndex, pageSize, sortField, sortOrder, searchFeedbackAdminRequest));
    }

    @Operation(summary = "修改反馈")
    @PutMapping("/{id}")
    public Result<Void> updateFeedback(@PathVariable Long id, @RequestBody @Validated UpdateFeedbackAdminRequest updateFeedbackAdminRequest){
        feedbackAdminService.updateFeedback(id, updateFeedbackAdminRequest);
        return Result.success();
    }

    @Operation(summary = "删除反馈")
    @DeleteMapping("/{id}")
    public Result<Void> deleteFeedback(@PathVariable Long id){
        feedbackAdminService.deleteFeedback(id);
        return Result.success();
    }
}
