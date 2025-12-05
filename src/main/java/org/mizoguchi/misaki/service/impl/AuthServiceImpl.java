package org.mizoguchi.misaki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
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
import org.mizoguchi.misaki.service.AuthService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, loginRequest.getEmail())
                .eq(User::getDeleteFlag, false));

        if (user == null) {
            throw new UserNotExistsException(FailMessageConstant.USER_NOT_EXISTS);
        } else if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new WrongPasswordException(FailMessageConstant.WRONG_PASSWORD);
        }

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, user.getId())
                .set(User::getLastLoginTime, LocalDateTime.now()));

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

        String verifyCode = (String) redisTemplate.opsForValue().get(registerRequest.getEmail());
        if (verifyCode == null) {
            throw new VerifyCodeExpiredException(FailMessageConstant.VERIFY_CODE_EXPIRED);
        } else if (!verifyCode.equals(registerRequest.getVerifyCode())) {
            throw new WrongVerifyCodeException(FailMessageConstant.WRONG_VERIFY_CODE);
        }

        String encryptPassword = passwordEncoder.encode(registerRequest.getPassword());

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(encryptPassword)
                .username(registerRequest.getUsername())
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
        String verifyCode = (String) redisTemplate.opsForValue().get(resetPasswordRequest.getEmail());

        if (verifyCode == null) {
            throw new VerifyCodeExpiredException(FailMessageConstant.VERIFY_CODE_EXPIRED);
        } else if (!verifyCode.equals(resetPasswordRequest.getVerifyCode())) {
            throw new WrongVerifyCodeException(FailMessageConstant.WRONG_VERIFY_CODE);
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
