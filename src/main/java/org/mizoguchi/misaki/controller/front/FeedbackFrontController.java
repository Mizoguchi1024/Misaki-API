package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.front.AddFeedbackFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.FeedbackFrontResponse;
import org.mizoguchi.misaki.service.front.FeedbackFrontService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/front/feedbacks")
@RequiredArgsConstructor
@Tag(name = "用户端-反馈相关接口")
public class FeedbackFrontController {
    private final FeedbackFrontService feedbackFrontService;

    @Operation(summary = "新建反馈")
    @PostMapping()
    public Result<Void> createFeedback(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestBody @Validated AddFeedbackFrontRequest addFeedbackFrontRequest) {
        feedbackFrontService.addFeedback(Long.valueOf(userDetails.getUsername()), addFeedbackFrontRequest);
        return Result.success();
    }

    @Operation(summary = "获取历史反馈")
    @GetMapping()
    public Result<List<FeedbackFrontResponse>> listFeedbacks(@AuthenticationPrincipal UserDetails userDetails){
        return Result.success(feedbackFrontService.listFeedbacks(Long.valueOf(userDetails.getUsername())));
    }

    @Operation(summary = "删除反馈")
    @DeleteMapping("/{id}")
    public Result<Void> deleteFeedback(@AuthenticationPrincipal UserDetails userDetails,
                                       @PathVariable @Positive Long id){
        feedbackFrontService.deleteFeedback(Long.valueOf(userDetails.getUsername()), id);
        return Result.success();
    }
}
