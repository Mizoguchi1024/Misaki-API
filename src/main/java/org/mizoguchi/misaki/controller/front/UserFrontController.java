package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.front.UpdateUserFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateSettingFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.UserFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.SettingFrontResponse;
import org.mizoguchi.misaki.service.front.UserFrontService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/front/users")
@RequiredArgsConstructor
@Tag(name = "用户端-用户相关接口")
public class UserFrontController {
    private final UserFrontService userFrontService;

    @Operation(summary = "每日签到")
    @PutMapping("/check-in")
    public Result<Void> checkIn(@AuthenticationPrincipal UserDetails authUser){
        userFrontService.checkIn(Long.valueOf(authUser.getUsername()));
        return Result.success();
    }

    @Operation(summary = "获取个人资料")
    @GetMapping("/profiles")
    public Result<UserFrontResponse> getProfile(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(userFrontService.getUser(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "修改个人资料")
    @PutMapping("/profiles")
    public Result<Void> updateProfile(@AuthenticationPrincipal UserDetails authUser,
                                      @RequestBody @Validated UpdateUserFrontRequest updateUserFrontRequest){
        userFrontService.updateUser(Long.valueOf(authUser.getUsername()), updateUserFrontRequest);
        return Result.success();
    }

    @Operation(summary = "获取设定")
    @GetMapping("/settings")
    public Result<SettingFrontResponse> getSetting(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(userFrontService.getSetting(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "修改设定")
    @PutMapping("/settings")
    public Result<Void> updateSetting(@AuthenticationPrincipal UserDetails authUser,
                                      @RequestBody @Validated UpdateSettingFrontRequest updateSettingFrontRequest){
        userFrontService.updateSetting(Long.valueOf(authUser.getUsername()), updateSettingFrontRequest);
        return Result.success();
    }

    @Operation(summary = "注销账号")
    @DeleteMapping
    public Result<Void> deleteUser(@AuthenticationPrincipal UserDetails authUser){
        userFrontService.deleteAccount(Long.valueOf(authUser.getUsername()));
        return Result.success();
    }
}
