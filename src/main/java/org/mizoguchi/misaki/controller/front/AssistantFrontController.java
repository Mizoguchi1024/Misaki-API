package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.front.AddAssistantFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateAssistantFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.AssistantFrontResponse;
import org.mizoguchi.misaki.service.front.AssistantFrontService;
import org.mizoguchi.misaki.service.front.LikesFrontService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/front/assistants")
@RequiredArgsConstructor
@Tag(name = "用户端-助手相关接口")
public class AssistantFrontController {
    private final AssistantFrontService assistantFrontService;
    private final LikesFrontService likesFrontService;

    @Operation(summary = "获取该用户拥有的指定的助手存档")
    @GetMapping("/{id}")
    public Result<AssistantFrontResponse> getAssistant(@AuthenticationPrincipal UserDetails authUser,
                                                       @PathVariable String id){
        return Result.success(assistantFrontService.getAssistant(Long.valueOf(authUser.getUsername()),
                Long.valueOf(id)));
    }

    @Operation(summary = "获取该用户拥有的助手存档")
    @GetMapping()
    public Result<List<AssistantFrontResponse>> listAssistants(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(assistantFrontService.listAssistants(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "获取市场公开的助手存档")
    @GetMapping("/public")
    public Result<List<AssistantFrontResponse>> listPublicAssistants(@AuthenticationPrincipal UserDetails authUser,
                                                                     @RequestParam @Positive Integer pageIndex,
                                                                     @RequestParam @Positive Integer pageSize){
        return Result.success(assistantFrontService.listPublicAssistants(Long.valueOf(authUser.getUsername()),
                pageIndex, pageSize));
    }

    @Operation(summary = "新建助手存档")
    @PostMapping()
    public Result<Void> createAssistant(@AuthenticationPrincipal UserDetails authUser,
                                        AddAssistantFrontRequest addAssistantFrontRequest){
        assistantFrontService.addAssistant(Long.valueOf(authUser.getUsername()), addAssistantFrontRequest);
        return Result.success();
    }

    @Operation(summary = "复制助手存档")
    @PostMapping("/{id}")
    public Result<Void> copyAssistant(@AuthenticationPrincipal UserDetails authUser, @PathVariable String id){
        assistantFrontService.copyAssistant(Long.valueOf(authUser.getUsername()), Long.valueOf(id));
        return Result.success();
    }

    @Operation(summary = "编辑助手存档")
    @PutMapping("/{id}")
    public Result<Void> updateAssistant(@AuthenticationPrincipal UserDetails authUser, @PathVariable String id,
                                        UpdateAssistantFrontRequest updateAssistantFrontRequest){
        assistantFrontService.updateAssistant(Long.valueOf(authUser.getUsername()), Long.valueOf(id),
                updateAssistantFrontRequest);
        return Result.success();
    }

    @Operation(summary = "为公开助手存档点赞")
    @PostMapping("/public/{id}/like")
    public Result<Void> likeAssistant(@AuthenticationPrincipal UserDetails authUser, @PathVariable String id) {
        likesFrontService.likeAssistant(Long.valueOf(authUser.getUsername()), Long.valueOf(id));
        return Result.success();
    }

    @Operation(summary = "删除助手存档")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAssistant(@AuthenticationPrincipal UserDetails authUser, @PathVariable String id){
        assistantFrontService.deleteAssistant(Long.valueOf(authUser.getUsername()), Long.valueOf(id));
        return Result.success();
    }
}
