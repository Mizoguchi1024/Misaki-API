package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.front.SendMessageFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.MessageFrontResponse;
import org.mizoguchi.misaki.service.ChatService;
import org.mizoguchi.misaki.service.MessageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/front/chats")
@RequiredArgsConstructor
@Tag(name = "会话相关接口")
public class ChatController {
    private final ChatService chatService;
    private final MessageService messageService;

    @Operation(summary = "新建会话")
    @PostMapping()
    public Result<Long> createChat(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(chatService.addChat(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "发送消息")
    @PostMapping(value = "/{id}/messages", produces = "text/event-stream;charset=utf-8")
    public Flux<String> sendMessage(@AuthenticationPrincipal UserDetails authUser,
                                    @PathVariable @Positive Long id,
                                    @RequestBody @Validated SendMessageFrontRequest sendMessageFrontRequest) {
        return messageService.sendMessage(Long.valueOf(authUser.getUsername()), id, sendMessageFrontRequest.getContent(),
                sendMessageFrontRequest.getPrefix());
    }

    @Operation(summary = "获取会话标题")
    @GetMapping("/{id}/title")
    public Result<String> getChatTitle(@AuthenticationPrincipal UserDetails authUser, @PathVariable Long id) {
        return Result.success(chatService.getChatTitle(Long.valueOf(authUser.getUsername()), id));
    }

    @Operation(summary = "获取历史会话")
    @GetMapping()
    public Result<List<ChatFrontResponse>> listChats(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(chatService.listChatsFrontResponse(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "获取会话中的所有消息")
    @GetMapping(value = "/{id}/messages")
    public Result<List<MessageFrontResponse>> listMessages(@AuthenticationPrincipal UserDetails authUser,
                                                           @PathVariable @Positive Long id){
        return Result.success(messageService.listMessagesFrontResponse(Long.valueOf(authUser.getUsername()), id));
    }
}