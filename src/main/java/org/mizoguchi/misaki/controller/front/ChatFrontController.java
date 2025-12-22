package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.front.SendMessageFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateChatTitleFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.ChatFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.MessageFrontResponse;
import org.mizoguchi.misaki.service.front.ChatFrontService;
import org.mizoguchi.misaki.service.front.MessageFrontService;
import org.springframework.http.MediaType;
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
    public Result<Long> createChat(@AuthenticationPrincipal UserDetails userDetails){
        return Result.success(chatFrontService.addChat(Long.valueOf(userDetails.getUsername())));
    }

    @Operation(summary = "生成提示词建议")
    @GetMapping("/{id}/prompts")
    public Result<List<String>> listPrompts(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long id ,
                                            @RequestParam @Positive Integer size){
        return Result.success(chatFrontService.listPrompts(Long.valueOf(userDetails.getUsername()), id, size));
    }

    @Operation(summary = "发送消息")
    @PostMapping(value = "/{id}/messages", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<String> sendMessage(@AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable @Positive Long id,
                                    @RequestBody @Validated SendMessageFrontRequest sendMessageFrontRequest) {
        return messageFrontService.sendMessage(Long.valueOf(userDetails.getUsername()), id,
                sendMessageFrontRequest);
    }

    @Operation(summary = "生成会话标题")
    @GetMapping(value = "/{id}/title", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<String> createChatTitle(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        return chatFrontService.addChatTitle(Long.valueOf(userDetails.getUsername()), id);
    }
    
    @Operation(summary = "修改会话标题")
    @PutMapping(value = "/{id}/title")
    public Result<Void> updateChatTitle(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id,
                                        @RequestBody @Validated UpdateChatTitleFrontRequest updateChatTitleFrontRequest){
        chatFrontService.updateChatTitle(Long.valueOf(userDetails.getUsername()), id, updateChatTitleFrontRequest.getTitle());
        return Result.success();
    }

    @Operation(summary = "获取历史会话")
    @GetMapping()
    public Result<List<ChatFrontResponse>> listChats(@AuthenticationPrincipal UserDetails userDetails){
        return Result.success(chatFrontService.listChats(Long.valueOf(userDetails.getUsername())));
    }

    @Operation(summary = "搜索会话")
    @GetMapping("/search")
    public Result<List<ChatFrontResponse>> searchChats(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestParam String keyword){
        return Result.success(chatFrontService.searchChats(Long.valueOf(userDetails.getUsername()), keyword));
    }

    @Operation(summary = "获取会话中的所有消息")
    @GetMapping("/{id}/messages")
    public Result<List<MessageFrontResponse>> listMessages(@AuthenticationPrincipal UserDetails userDetails,
                                                           @PathVariable @Positive Long id){
        return Result.success(messageFrontService.listMessages(Long.valueOf(userDetails.getUsername()), id));
    }

    @Operation(summary = "删除会话")
    @DeleteMapping("/{id}")
    public Result<Void> deleteChat(@AuthenticationPrincipal UserDetails userDetails, @PathVariable @Positive Long id){
        chatFrontService.deleteChat(Long.valueOf(userDetails.getUsername()), id);
        return Result.success();
    }

    @Operation(summary = "删除全部会话")
    @DeleteMapping()
    public Result<Void> deleteAllChats(@AuthenticationPrincipal UserDetails userDetails){
        chatFrontService.deleteAllChats(Long.valueOf(userDetails.getUsername()));
        return Result.success();
    }
}