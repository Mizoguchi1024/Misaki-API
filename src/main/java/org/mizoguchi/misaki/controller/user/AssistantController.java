package org.mizoguchi.misaki.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.entity.dto.CreateAssistantRequest;
import org.mizoguchi.misaki.entity.dto.EditAssistantRequest;
import org.mizoguchi.misaki.entity.vo.UserAssistantResponse;
import org.mizoguchi.misaki.service.AssistantService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/user/assistants")
@RequiredArgsConstructor
@Tag(name = "助手相关接口")
public class AssistantController {
    private final AssistantService assistantService;

    @Operation(summary = "获取该用户拥有的助手")
    @GetMapping()
    public Result<List<UserAssistantResponse>> getAssistants(@AuthenticationPrincipal UserDetails authUser){
        List<UserAssistantResponse> assistants = assistantService.getAssistants(Long.valueOf(authUser.getUsername())).stream()
                .map(assistant -> {
                    UserAssistantResponse userAssistantResponse = new UserAssistantResponse();
                    BeanUtils.copyProperties(assistant, userAssistantResponse);
                    return userAssistantResponse;
                }).collect(Collectors.toList());

        return Result.success(assistants);
    }

    @Operation(summary = "获取市场公开的助手")
    @GetMapping("/public")
    public Result<List<UserAssistantResponse>> getPublicAssistants(@AuthenticationPrincipal UserDetails authUser){
        List<UserAssistantResponse> assistants = assistantService.getPublicAssistants(Long.valueOf(authUser.getUsername())).stream()
                .map(assistant -> {
                    UserAssistantResponse userAssistantResponse = new UserAssistantResponse();
                    BeanUtils.copyProperties(assistant, userAssistantResponse);
                    return userAssistantResponse;
                }).collect(Collectors.toList());

        return Result.success(assistants);
    }

    @Operation(summary = "新建助手设定")
    @PostMapping()
    public Result<Void> createAssistant(@AuthenticationPrincipal UserDetails authUser,
                                        CreateAssistantRequest createAssistantRequest){
        assistantService.createAssistant(Long.valueOf(authUser.getUsername()), createAssistantRequest);
        return Result.success();
    }

    @Operation(summary = "编辑助手设定")
    @PutMapping("/{id}")
    public Result<Void> editAssistant(@AuthenticationPrincipal UserDetails authUser, @PathVariable Long id,
                                      EditAssistantRequest editAssistantRequest){
        return Result.success();
    }

}
