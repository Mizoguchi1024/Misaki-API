package org.mizoguchi.misaki.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.vo.admin.WorkspaceDataAdminResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/admin/workspace")
@RequiredArgsConstructor
@Tag(name = "管理端-工作台相关接口")
public class WorkspaceAdminController {

    @Operation(summary = "获取工作台数据")
    @GetMapping()
    public Result<WorkspaceDataAdminResponse> getWorkspaceData() {
        return Result.success();
    }
}
