package org.mizoguchi.misaki.controller.admin;

import java.util.List;

import org.mizoguchi.misaki.annotation.EnableRateLimit;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.vo.admin.McpServerAdminResponse;
import org.mizoguchi.misaki.service.admin.McpAdminService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/admin/mcp")
@RequiredArgsConstructor
@Tag(name = "管理端-MCP相关接口")
public class McpAdminController {
    private final McpAdminService mcpAdminService;

    @EnableRateLimit()
    @Operation(summary = "获取MCP服务器")
    @GetMapping()
    public Result<List<McpServerAdminResponse>> listMcpServers() {
        return Result.success(mcpAdminService.listMcpServers());
    }
}
