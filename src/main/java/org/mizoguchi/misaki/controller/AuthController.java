package org.mizoguchi.misaki.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.entity.dto.LoginRequest;
import org.mizoguchi.misaki.entity.dto.RegisterRequest;
import org.mizoguchi.misaki.entity.dto.ResetPasswordRequest;
import org.mizoguchi.misaki.entity.vo.LoginResponse;
import org.mizoguchi.misaki.service.EmailService;
import org.mizoguchi.misaki.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证相关接口")
public class AuthController {
    private final UserService userService;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Validated LoginRequest loginRequest) {
        return Result.success(userService.login(loginRequest));
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Validated RegisterRequest registerRequest) {
        userService.register(registerRequest);
        return Result.success();
    }

    @Operation(summary = "重设密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody @Validated ResetPasswordRequest resetPasswordRequest){
        userService.resetPassword(resetPasswordRequest);
        return Result.success();
    }

    @Operation(summary = "发送电子邮箱验证码")
    @GetMapping("/verify/{email}")
    public Result<Void> sendVerifyCode(@PathVariable @Email() String email){
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000)); //六位验证码
        emailService.sendVerificationEmail(email, code);
        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(5));

        return Result.success();
    }
}
