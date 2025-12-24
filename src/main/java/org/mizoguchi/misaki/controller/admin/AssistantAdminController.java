package org.mizoguchi.misaki.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.InvalidSortParamsException;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.admin.AddAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.vo.admin.AssistantAdminResponse;
import org.mizoguchi.misaki.service.admin.AssistantAdminService;
import org.springframework.data.util.ParsingUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/assistants")
@RequiredArgsConstructor
@Tag(name = "管理端-助手相关接口")
public class AssistantAdminController {
    private final AssistantAdminService assistantAdminService;

    @Operation(summary = "创建助手")
    @PostMapping()
    public Result<Void> createAssistant(@RequestBody @Validated AddAssistantAdminRequest addAssistantAdminRequest){
        assistantAdminService.addAssistant(addAssistantAdminRequest);
        return Result.success();
    }

    @Operation(summary = "分页条件搜索助手")
    @PostMapping("/search")
    public Result<List<AssistantAdminResponse>> searchAssistants(@RequestParam @Positive Integer pageIndex,
                                                                 @RequestParam @Positive Integer pageSize,
                                                                 @RequestParam(required = false) String sortField,
                                                                 @RequestParam(defaultValue = "asc") String sortOrder,
                                                                 @RequestBody @Validated SearchAssistantAdminRequest searchAssistantAdminRequest){
        if (StringUtils.hasText(sortField)){
            try {
                Assistant.class.getDeclaredField(sortField);
            } catch (NoSuchFieldException e) {
                throw new InvalidSortParamsException(FailMessageConstant.INVALID_SORT_PARAMS);
            }
            sortField = ParsingUtils.reconcatenateCamelCase(sortField, "_");
        }
        return Result.success(assistantAdminService.searchAssistants(pageIndex, pageSize, sortField, sortOrder, searchAssistantAdminRequest));
    }

    @Operation(summary = "修改助手")
    @PutMapping("/{id}")
    public Result<Void> updateAssistant(@PathVariable Long id, @RequestBody @Validated UpdateAssistantAdminRequest updateAssistantAdminRequest){
        assistantAdminService.updateAssistant(id, updateAssistantAdminRequest);
        return Result.success();
    }

    @Operation(summary = "删除助手")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAssistant(@PathVariable Long id){
        assistantAdminService.deleteAssistant(id);
        return Result.success();
    }
}
