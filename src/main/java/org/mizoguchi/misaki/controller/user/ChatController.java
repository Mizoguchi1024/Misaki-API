package org.mizoguchi.misaki.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.entity.dto.SendMessageRequest;
import org.mizoguchi.misaki.entity.vo.UserConversationVo;
import org.mizoguchi.misaki.entity.vo.UserMessageVo;
import org.mizoguchi.misaki.service.ChatService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/user/conversations")
@RequiredArgsConstructor
@Tag(name = "会话相关接口")
public class ChatController {
    private final ChatService chatService;

    @Operation(summary = "新建会话")
    @PostMapping()
    public Result<Long> createConversation(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(chatService.createConversation(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "发送消息")
    @PostMapping(value = "/{id}/messages", produces = "text/event-stream;charset=utf-8")
    public Flux<String> sendMessage(@AuthenticationPrincipal UserDetails authUser,
                                    @PathVariable @Positive Long id,
                                    @RequestBody @Validated SendMessageRequest sendMessageRequest) {
        return chatService.sendMessage(Long.valueOf(authUser.getUsername()), id, sendMessageRequest.getContent(),
                sendMessageRequest.getPrefix());
    }

    @Operation(summary = "获取会话标题")
    @GetMapping("/{id}/title")
    public Result<String> getTitle(@AuthenticationPrincipal UserDetails authUser, @PathVariable Long id) {
        return Result.success(chatService.getTitle(Long.valueOf(authUser.getUsername()), id));
    }

    @Operation(summary = "获取历史会话")
    @GetMapping()
    public Result<List<UserConversationVo>> getConversations(@AuthenticationPrincipal UserDetails authUser){
        List<UserConversationVo> conversations = chatService.getConversations(Long.valueOf(authUser.getUsername())).stream()
                .map(conversation -> {
                    UserConversationVo userConversationVo = new UserConversationVo();
                    BeanUtils.copyProperties(conversation, userConversationVo);
                    return userConversationVo;
                }).collect(Collectors.toList());

        return Result.success(conversations);
    }

    @Operation(summary = "获取会话中的所有消息")
    @GetMapping(value = "/{id}/messages")
    public Result<List<UserMessageVo>> getMessages(@AuthenticationPrincipal UserDetails authUser,
                                                   @PathVariable @Positive Long id){
        List<UserMessageVo> messages = chatService.getMessages(Long.valueOf(authUser.getUsername()), id).stream()
                .map(message -> {
                    UserMessageVo userMessageVo = new UserMessageVo();
                    BeanUtils.copyProperties(message, userMessageVo);
                    return userMessageVo;
                }).collect(Collectors.toList());

        return Result.success(messages);
    }
}