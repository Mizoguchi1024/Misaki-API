package org.mizoguchi.misaki.controller.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.front.UpdateUserFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateSettingFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.UserFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.SettingFrontResponse;
import org.mizoguchi.misaki.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/front/users")
@RequiredArgsConstructor
@Tag(name = "用户相关接口")
public class UserController {
    private final UserService userService;

    @Operation(summary = "每日签到")
    @PutMapping("/check-in")
    public Result<Void> checkIn(@AuthenticationPrincipal UserDetails authUser){
        userService.checkIn(Long.valueOf(authUser.getUsername()));
        return Result.success();
    }

    @Operation(summary = "获取个人资料")
    @GetMapping("/profiles")
    public Result<UserFrontResponse> getProfile(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(userService.getUserFrontResponse(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "修改个人资料")
    @PutMapping("/profiles")
    public Result<Void> updateProfile(@AuthenticationPrincipal UserDetails authUser,
                                      @RequestBody @Validated UpdateUserFrontRequest updateUserFrontRequest){
        userService.updateUser(Long.valueOf(authUser.getUsername()), updateUserFrontRequest);
        return Result.success();
    }

    @Operation(summary = "获取设定")
    @GetMapping("/settings")
    public Result<SettingFrontResponse> getSetting(@AuthenticationPrincipal UserDetails authUser){
        return Result.success(userService.getSettingFrontResponse(Long.valueOf(authUser.getUsername())));
    }

    @Operation(summary = "修改设定")
    @PutMapping("/settings")
    public Result<Void> updateSetting(@AuthenticationPrincipal UserDetails authUser,
                                      @RequestBody @Validated UpdateSettingFrontRequest updateSettingFrontRequest){
        userService.updateSetting(Long.valueOf(authUser.getUsername()), updateSettingFrontRequest);
        return Result.success();
    }

    @Operation(summary = "注销账号")
    @DeleteMapping
    public Result<Void> deleteUser(@AuthenticationPrincipal UserDetails authUser){
        userService.deleteAccount(Long.valueOf(authUser.getUsername()));
        return Result.success();
    }
}
