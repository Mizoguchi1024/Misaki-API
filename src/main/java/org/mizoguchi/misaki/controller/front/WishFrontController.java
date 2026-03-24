package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.annotation.EnableRateLimit;
import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.common.result.Result;
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

    @EnableRateLimit()
    @Operation(summary = "购买拼图")
    @PostMapping("/puzzles")
    public Result<Void> buyPuzzle(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam @Min(1) @Max(100) Integer amount,
                                  @RequestParam @Pattern(regexp = "crystal|stardust") String currency) {
        if (currency.equals("crystal")) {
            wishFrontService.buyPuzzleWithCrystal(Long.valueOf(userDetails.getUsername()), amount);
        }else if (currency.equals("stardust")){
            wishFrontService.buyPuzzleWithStardust(Long.valueOf(userDetails.getUsername()), amount);
        }

        return Result.success();
    }

    @EnableRateLimit()
    @Operation(summary = "抽卡")
    @PostMapping("/gacha")
    public Result<List<WishFrontResponse>> gacha(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestParam @Min(1) @Max(10) Integer times){
        return Result.success(wishFrontService.gacha(Long.valueOf(userDetails.getUsername()), times));
    }

    @EnableRateLimit()
    @Operation(summary = "抽卡历史记录")
    @GetMapping("/gacha")
    public Result<PageResult<WishFrontResponse>> wishHistory(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestParam @Positive Integer pageIndex,
                                                       @RequestParam @Positive Integer pageSize){
        return Result.success(wishFrontService.listWishes(Long.valueOf(userDetails.getUsername()), pageIndex, pageSize));
    }
}
