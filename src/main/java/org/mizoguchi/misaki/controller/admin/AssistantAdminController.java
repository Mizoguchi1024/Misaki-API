package org.mizoguchi.misaki.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.admin.AddAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.vo.admin.AssistantAdminResponse;
import org.mizoguchi.misaki.service.admin.AssistantAdminService;
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
    public Result<Void> createAssistant(@RequestBody AddAssistantAdminRequest addAssistantAdminRequest){
        assistantAdminService.addAssistant(addAssistantAdminRequest);
        return Result.success();
    }

    @Operation(summary = "分页查询所有助手")
    @GetMapping()
    public Result<List<AssistantAdminResponse>> listAssistants(@RequestParam @Positive Integer pageIndex,
                                                               @RequestParam @Positive Integer pageSize){
        return Result.success(assistantAdminService.listAssistants(pageIndex, pageSize));
    }

    @Operation(summary = "条件搜索助手")
    @GetMapping("/search")
    public Result<List<AssistantAdminResponse>> searchAssistants(SearchAssistantAdminRequest searchAssistantAdminRequest){
        return Result.success(assistantAdminService.searchAssistants(searchAssistantAdminRequest));
    }

    @Operation(summary = "修改助手")
    @PutMapping("/{id}")
    public Result<Void> updateAssistant(@PathVariable Long id, @RequestBody UpdateAssistantAdminRequest updateAssistantAdminRequest){
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
