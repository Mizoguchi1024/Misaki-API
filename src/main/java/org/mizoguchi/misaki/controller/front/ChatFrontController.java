package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.front.SendMessageFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.MessageFrontResponse;
import org.mizoguchi.misaki.service.front.ChatFrontService;
import org.mizoguchi.misaki.service.front.MessageFrontService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@Validated
@RestController
@RequestMapping("/front/chats")
@RequiredArgsConstructor
@Tag(name = "用户端-会话相关接口")
public class ChatFrontController {
    private final ChatFrontService chatFrontService;
    private final MessageFrontService messageFrontService;

    @Operation(summary = "新建会话")
    @PostMapping()
    public Result<Long> createChat(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(chatFrontService.addChat(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "发送消息")
    @PostMapping(value = "/{id}/messages", produces = "text/event-stream;charset=utf-8")
    public Flux<String> sendMessage(@AuthenticationPrincipal UserDetails authUser,
                                    @PathVariable @Positive String id,
                                    @RequestBody @Validated SendMessageFrontRequest sendMessageFrontRequest) {
        return messageFrontService.sendMessage(Long.valueOf(authUser.getUsername()), Long.valueOf(id),
                sendMessageFrontRequest);
    }

    @Operation(summary = "生成会话标题")
    @GetMapping(value = "/{id}/title", produces = "text/event-stream;charset=utf-8")
    public Flux<String> createChatTitle(@AuthenticationPrincipal UserDetails authUser, @PathVariable String id) {
        return chatFrontService.addChatTitle(Long.valueOf(authUser.getUsername()), Long.valueOf(id));
    }

    @Operation(summary = "获取历史会话")
    @GetMapping()
    public Result<List<ChatFrontResponse>> listChats(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(chatFrontService.listChats(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "搜索会话")
    @GetMapping("/search")
    public Result<List<ChatFrontResponse>> searchChats(@AuthenticationPrincipal UserDetails authUser,
                                                       @RequestParam String keyword){
        return Result.success(chatFrontService.searchChats(Long.valueOf(authUser.getUsername()), keyword));
    }

    @Operation(summary = "获取会话中的所有消息")
    @GetMapping("/{id}/messages")
    public Result<List<MessageFrontResponse>> listMessages(@AuthenticationPrincipal UserDetails authUser,
                                                           @PathVariable @Positive String id){
        return Result.success(messageFrontService.listMessages(Long.valueOf(authUser.getUsername()),
                Long.valueOf(id)));
    }

    @Operation(summary = "删除会话")
    @DeleteMapping("/{id}")
    public Result<Void> deleteChat(@AuthenticationPrincipal UserDetails authUser, @PathVariable @Positive String id){
        chatFrontService.deleteChat(Long.valueOf(authUser.getUsername()), Long.valueOf(id));
        return Result.success();
    }
}