package org.mizoguchi.misaki.controller.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.EmailConstant;
import org.mizoguchi.misaki.common.constant.RedisConstant;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.common.LoginRequest;
import org.mizoguchi.misaki.pojo.dto.common.RegisterRequest;
import org.mizoguchi.misaki.pojo.dto.common.ResetPasswordRequest;
import org.mizoguchi.misaki.pojo.vo.common.LoginResponse;
import org.mizoguchi.misaki.service.common.AuthService;
import org.mizoguchi.misaki.service.common.EmailService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Random;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证相关接口")
public class AuthController {
    private final AuthService authService;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Validated LoginRequest loginRequest) {
        return Result.success(authService.login(loginRequest));
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Validated RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return Result.success();
    }

    @Operation(summary = "重设密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody @Validated ResetPasswordRequest resetPasswordRequest){
        authService.resetPassword(resetPasswordRequest);
        return Result.success();
    }

    @Operation(summary = "发送电子邮箱验证码")
    @PostMapping("/verification/{email}")
    public Result<Void> sendVerificationCode(@PathVariable @Email() String email){
        Random random = new Random(System.currentTimeMillis());
        String code = String.format(EmailConstant.VERIFICATION_CODE_FORMAT,random.nextInt(EmailConstant.VERIFICATION_CODE_LIMIT));

        emailService.sendVerificationEmail(email, EmailConstant.VERIFICATION_EMAIL_SUBJECT, code);
        redisTemplate.opsForValue().set(RedisConstant.EMAIL + email, code, Duration.ofMinutes(5)); // 覆盖旧值

        return Result.success();
    }
}
