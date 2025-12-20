package org.mizoguchi.misaki.service.common.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RedisConstant;
import org.mizoguchi.misaki.common.exception.*;
import org.mizoguchi.misaki.common.util.JwtUtil;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.entity.Settings;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.dto.common.LoginRequest;
import org.mizoguchi.misaki.pojo.dto.common.RegisterRequest;
import org.mizoguchi.misaki.pojo.dto.common.ResetPasswordRequest;
import org.mizoguchi.misaki.pojo.vo.common.LoginResponse;
import org.mizoguchi.misaki.mapper.SettingsMapper;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.service.common.AuthService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final SettingsMapper settingsMapper;
    private final AssistantMapper assistantMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String key = RedisConstant.PASSWORD_RETRY + loginRequest.getEmail();

        String value = redisTemplate.opsForValue().get(key);
        int retryCount = value == null ? 0 : Integer.parseInt(value);

        if (retryCount >= 5) {
            throw new AccountTemporarilyLockedException(FailMessageConstant.ACCOUNT_TEMPORARILY_LOCKED);
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, loginRequest.getEmail())
                .eq(User::getDeleteFlag, false));

        if (user == null) {
            throw new UserNotExistsException(FailMessageConstant.USER_NOT_EXISTS);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            Long count = redisTemplate.opsForValue().increment(key);

            if (count != null && count == 1L) {
                redisTemplate.expire(key, Duration.ofMinutes(5));
            }

            throw new WrongPasswordException(FailMessageConstant.WRONG_PASSWORD);
        }

        redisTemplate.delete(key);

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, user.getId())
                .set(User::getLastLoginTime, LocalDateTime.now())
                .set(User::getDeletePendingFlag, false));

        return LoginResponse.builder()
                .token(jwtUtil.generateToken(user.getId().toString(), user.getAuthRole()))
                .authRole(user.getAuthRole())
                .build();
    }

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        User existingUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, registerRequest.getEmail())
                .eq(User::getDeleteFlag, false));

        if (existingUser != null) {
            throw new UserAlreadyExistsException(FailMessageConstant.USER_ALREADY_EXISTS);
        }

        String verificationCode = redisTemplate.opsForValue().get(RedisConstant.EMAIL + registerRequest.getEmail());
        if (verificationCode == null) {
            throw new VerificationCodeExpiredException(FailMessageConstant.VERIFICATION_CODE_EXPIRED);
        } else if (!verificationCode.equals(registerRequest.getVerificationCode())) {
            throw new WrongVerificationCodeException(FailMessageConstant.WRONG_VERIFICATION_CODE);
        }

        String encryptPassword = passwordEncoder.encode(registerRequest.getPassword());

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(encryptPassword)
                .username(registerRequest.getUsername())
                .token(100000)
                .deletePendingFlag(false)
                .build();

        userMapper.insert(user);

        Settings settings = Settings.builder()
                .userId(user.getId())
                .build();

        settingsMapper.insert(settings);

        Assistant assistant = Assistant.builder()
                .creatorId(user.getId())
                .ownerId(user.getId())
                .build();

        assistantMapper.insert(assistant);

        // TODO 插入别的必须的记录
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String verificationCode = redisTemplate.opsForValue().get(RedisConstant.EMAIL + resetPasswordRequest.getEmail());

        if (verificationCode == null) {
            throw new VerificationCodeExpiredException(FailMessageConstant.VERIFICATION_CODE_EXPIRED);
        } else if (!verificationCode.equals(resetPasswordRequest.getVerificationCode())) {
            throw new WrongVerificationCodeException(FailMessageConstant.WRONG_VERIFICATION_CODE);
        }

        String encryptPassword = passwordEncoder.encode(resetPasswordRequest.getPassword());

        int affectedRows = userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getEmail, resetPasswordRequest.getEmail())
                .eq(User::getDeleteFlag, false)
                .set(User::getPassword, encryptPassword));

        if (affectedRows == 0) {
            throw new UserNotExistsException(FailMessageConstant.USER_NOT_EXISTS);
        }
    }
}
