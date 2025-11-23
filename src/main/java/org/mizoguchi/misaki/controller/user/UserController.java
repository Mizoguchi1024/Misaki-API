package org.mizoguchi.misaki.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.entity.Setting;
import org.mizoguchi.misaki.entity.User;
import org.mizoguchi.misaki.entity.dto.EditProfileRequest;
import org.mizoguchi.misaki.entity.dto.EditSettingRequest;
import org.mizoguchi.misaki.entity.vo.UserProfileVo;
import org.mizoguchi.misaki.entity.vo.UserSettingVo;
import org.mizoguchi.misaki.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/user/users")
@RequiredArgsConstructor
@Tag(name = "用户相关接口")
public class UserController {
    private final UserService userService;

    @Operation(summary = "获取个人资料")
    @GetMapping("/profiles")
    public Result<UserProfileVo> getProfile(@AuthenticationPrincipal UserDetails authUser){
        User user = userService.getProfile(Long.valueOf(authUser.getUsername()));

        UserProfileVo userProfileVo = new UserProfileVo();
        BeanUtils.copyProperties(user, userProfileVo);

        return Result.success(userProfileVo);
    }

    @Operation(summary = "修改个人资料")
    @PutMapping("/profiles")
    public Result<Void> editProfile(@AuthenticationPrincipal UserDetails authUser,
                                    @RequestBody @Validated EditProfileRequest editProfileRequest){
        userService.editProfile(Long.valueOf(authUser.getUsername()), editProfileRequest);

        return Result.success();
    }

    @Operation(summary = "获取设定")
    @GetMapping("/settings")
    public Result<UserSettingVo> getSetting(@AuthenticationPrincipal UserDetails authUser){
        Setting setting = userService.getSetting(Long.valueOf(authUser.getUsername()));

        UserSettingVo userSettingVo = new UserSettingVo();
        BeanUtils.copyProperties(setting, userSettingVo);

        return Result.success(userSettingVo);
    }

    @Operation(summary = "修改设定")
    @PutMapping("/settings")
    public Result<Void> editSetting(@AuthenticationPrincipal UserDetails authUser,
                                    @RequestBody @Validated EditSettingRequest editSettingRequest){
        userService.editSetting(Long.valueOf(authUser.getUsername()), editSettingRequest);

        return Result.success();
    }

    @Operation(summary = "注销账号")
    @DeleteMapping
    public Result<Void> deleteUser(@AuthenticationPrincipal UserDetails authUser){
        userService.deleteUser(Long.valueOf(authUser.getUsername()));

        return Result.success();
    }
}
