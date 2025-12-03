package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.service.WishService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/front/wish")
@RequiredArgsConstructor
@Tag(name = "祈愿相关接口")
public class WishController {
    private final WishService wishService;

    @Operation(summary = "购买拼图")
    @PutMapping("/puzzle/{amount}")
    public Result<Void> buyPuzzle(@AuthenticationPrincipal UserDetails authUser, @PathVariable @Positive Integer amount) {
        wishService.buyPuzzle(Long.valueOf(authUser.getUsername()), amount);
        return Result.success();
    }

    @Operation(summary = "购买模型")
    @PostMapping("/model/{id}")
    public Result<Void> buyModel(@AuthenticationPrincipal UserDetails authUser, @PathVariable @Positive Long id){
        wishService.buyModel(Long.valueOf(authUser.getUsername()), id);
        return Result.success();
    }

    @Operation(summary = "抽卡")
    @PostMapping("/{times}")
    public Result<Void> wish(@AuthenticationPrincipal UserDetails authUser, @PathVariable @Positive Integer times){
        wishService.wish(Long.valueOf(authUser.getUsername()), times);
        return Result.success();
    }
}
