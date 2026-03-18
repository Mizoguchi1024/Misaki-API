package org.mizoguchi.misaki.controller.front;

import java.util.List;

import org.mizoguchi.misaki.annotation.EnableRateLimit;
import org.mizoguchi.misaki.common.enumeration.LikesTargetTypeEnum;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.vo.front.McpServerFrontResponse;
import org.mizoguchi.misaki.service.front.LikesFrontService;
import org.mizoguchi.misaki.service.front.McpFrontService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/front/mcp")
@RequiredArgsConstructor
@Tag(name = "用户端-MCP页相关接口")
public class McpFrontController {
    private final LikesFrontService likesFrontService;
    private final McpFrontService mcpFrontService;
    
    @EnableRateLimit()
    @Operation(summary = "获取MCP服务器")
    @GetMapping()
    public Result<List<McpServerFrontResponse>> listMcpServers() {
        return Result.success(mcpFrontService.listMcpServers());
    }
    
    @EnableRateLimit()
    @Operation(summary = "点赞MCP服务器")
    @GetMapping("/{id}/like")
    public Result<Void> likeMcpServer(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        likesFrontService.likeObject(Long.valueOf(userDetails.getUsername()), LikesTargetTypeEnum.MCP.getValue(), id);
        return Result.success();
    }
}
