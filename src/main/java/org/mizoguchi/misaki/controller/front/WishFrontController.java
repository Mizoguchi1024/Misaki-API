package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.vo.front.ModelFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.WishFrontResponse;
import org.mizoguchi.misaki.service.front.WishFrontService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/front/wish")
@RequiredArgsConstructor
@Tag(name = "用户端-祈愿相关接口")
public class WishFrontController {
    private final WishFrontService wishFrontService;

    @Operation(summary = "购买拼图")
    @PostMapping("/puzzles")
    public Result<Void> buyPuzzle(@AuthenticationPrincipal UserDetails authUser, @RequestParam @Positive Integer amount,
                                  @RequestParam String currency) {
        if (currency.equals("crystal")) {
            wishFrontService.buyPuzzleWithCrystal(Long.valueOf(authUser.getUsername()), amount);
        }else if (currency.equals("stardust")){
            wishFrontService.buyPuzzleWithStardust(Long.valueOf(authUser.getUsername()), amount);
        }

        return Result.success();
    }

    @Operation(summary = "获取可购买模型")
    @GetMapping("/models")
    public Result<List<ModelFrontResponse>> listModels(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(wishFrontService.listModels(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "购买模型")
    @PostMapping("/models/{id}")
    public Result<Void> buyModel(@AuthenticationPrincipal UserDetails authUser, @PathVariable @Positive String id){
        wishFrontService.buyModel(Long.valueOf(authUser.getUsername()), Long.valueOf(id));
        return Result.success();
    }

    @Operation(summary = "抽卡")
    @PostMapping("/gacha")
    public Result<WishFrontResponse> gacha(@AuthenticationPrincipal UserDetails authUser,
                                           @RequestParam @Positive Integer times){
        return Result.success(wishFrontService.gacha(Long.valueOf(authUser.getUsername()), times));
    }

    @Operation(summary = "抽卡历史记录")
    @GetMapping("/gacha/history")
    public Result<List<WishFrontResponse>> wishHistory(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(wishFrontService.listWishes(Long.valueOf(authUser.getUsername())));
    }
}
