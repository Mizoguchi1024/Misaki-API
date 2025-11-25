package org.mizoguchi.misaki.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.entity.dto.front.AddAssistantFrontRequest;
import org.mizoguchi.misaki.entity.dto.front.UpdateAssistantFrontRequest;
import org.mizoguchi.misaki.entity.vo.front.AssistantFrontResponse;
import org.mizoguchi.misaki.service.AssistantService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/front/assistants")
@RequiredArgsConstructor
@Tag(name = "助手相关接口")
public class AssistantController {
    private final AssistantService assistantService;

    @Operation(summary = "获取该用户拥有的助手")
    @GetMapping()
    public Result<List<AssistantFrontResponse>> listAssistants(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(assistantService.listAssistantsFrontResponse(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "获取市场公开的助手")
    @GetMapping("/public")
    public Result<List<AssistantFrontResponse>> listPublicAssistants(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(assistantService.listPublicAssistantsFrontResponse(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "新建助手设定")
    @PostMapping()
    public Result<Void> createAssistant(@AuthenticationPrincipal UserDetails authUser,
                                        AddAssistantFrontRequest addAssistantFrontRequest){
        assistantService.addAssistant(Long.valueOf(authUser.getUsername()), addAssistantFrontRequest);
        return Result.success();
    }

    @Operation(summary = "编辑助手设定")
    @PutMapping("/{id}")
    public Result<Void> updateAssistant(@AuthenticationPrincipal UserDetails authUser, @PathVariable Long id,
                                        UpdateAssistantFrontRequest updateAssistantFrontRequest){
        return Result.success();
    }

}
