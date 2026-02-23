package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.annotation.EnableRateLimit;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.vo.front.AboutFrontResponse;
import org.mizoguchi.misaki.service.front.LikesFrontService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/front/about")
@RequiredArgsConstructor
@Tag(name = "用户端-关于页相关接口")
public class AboutFrontController {
    private final LikesFrontService likesFrontService;

    @EnableRateLimit()
    @Operation(summary = "为应用点赞")
    @PostMapping("/like")
    public Result<Void> likeMisaki(@AuthenticationPrincipal UserDetails userDetails) {
        likesFrontService.likeMisaki(Long.valueOf(userDetails.getUsername()));
        return Result.success();
    }

    @EnableRateLimit()
    @Operation(summary = "获取应用点赞数")
    @GetMapping("/like")
    public Result<AboutFrontResponse> getMisakiLikes(@AuthenticationPrincipal UserDetails userDetails){
        return Result.success(likesFrontService.getMisakiLikes(Long.valueOf(userDetails.getUsername())));
    }
}
