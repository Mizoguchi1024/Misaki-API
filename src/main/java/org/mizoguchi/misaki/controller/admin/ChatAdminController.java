package org.mizoguchi.misaki.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.InvalidSortParamsException;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.admin.SearchChatAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateChatAdminRequest;
import org.mizoguchi.misaki.pojo.entity.Chat;
import org.mizoguchi.misaki.pojo.vo.admin.ChatAdminResponse;
import org.mizoguchi.misaki.service.admin.ChatAdminService;
import org.springframework.data.util.ParsingUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/chats")
@RequiredArgsConstructor
@Tag(name = "管理端-会话相关接口")
public class ChatAdminController {
    private final ChatAdminService chatAdminService;

    @Operation(summary = "分页条件搜索会话")
    @PostMapping("/search")
    public Result<List<ChatAdminResponse>> searchChats(@RequestParam @Positive Integer pageIndex,
                                                       @RequestParam @Positive Integer pageSize,
                                                       @RequestParam(required = false) String sortField,
                                                       @RequestParam(defaultValue = "asc") String sortOrder,
                                                       @RequestBody @Validated SearchChatAdminRequest searchChatAdminRequest){
        if (sortField != null && !sortField.isBlank()){
            try {
                Chat.class.getDeclaredField(sortField);
            } catch (NoSuchFieldException e) {
                throw new InvalidSortParamsException(FailMessageConstant.INVALID_SORT_PARAMS);
            }
            sortField = ParsingUtils.reconcatenateCamelCase(sortField, "_");
        }
        return Result.success(chatAdminService.searchChats(pageIndex, pageSize, sortField, sortOrder, searchChatAdminRequest));
    }

    @Operation(summary = "修改会话")
    @PutMapping("/{id}")
    public Result<Void> updateChat(@PathVariable Long id, @RequestBody @Validated UpdateChatAdminRequest updateChatAdminRequest){
        chatAdminService.updateChat(id, updateChatAdminRequest);
        return Result.success();
    }

    @Operation(summary = "删除会话")
    @DeleteMapping("/{id}")
    public Result<Void> deleteChat(@PathVariable Long id){
        chatAdminService.deleteChat(id);
        return Result.success();
    }
}
