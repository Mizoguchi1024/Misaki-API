package org.mizoguchi.misaki.service.impl;

import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.MessageConstant;
import org.mizoguchi.misaki.common.exception.*;
import org.mizoguchi.misaki.common.util.JwtUtil;
import org.mizoguchi.misaki.entity.Settings;
import org.mizoguchi.misaki.entity.User;
import org.mizoguchi.misaki.entity.dto.common.LoginRequest;
import org.mizoguchi.misaki.entity.dto.common.RegisterRequest;
import org.mizoguchi.misaki.entity.dto.common.ResetPasswordRequest;
import org.mizoguchi.misaki.entity.vo.common.LoginResponse;
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
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userMapper.selectUserByEmail(loginRequest.getEmail());

        if (user == null) {
            throw new UserNotExistsException(MessageConstant.USER_NOT_EXISTS);
        } else if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new WrongPasswordException(MessageConstant.WRONG_PASSWORD);
        }

        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateUserById(user);

        return LoginResponse.builder()
                .token(jwtUtil.generateToken(user.getId().toString(), user.getAuthRole()))
                .authRole(user.getAuthRole())
                .build();
    }

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        User user = userMapper.selectUserByEmail(registerRequest.getEmail());

        if (user != null) {
            throw new UserAlreadyExistsException(MessageConstant.USER_ALREADY_EXISTS);
        }

        String verifyCode = (String) redisTemplate.opsForValue().get(registerRequest.getEmail());
        if (verifyCode == null) {
            throw new VerifyCodeExpiredException(MessageConstant.VERIFY_CODE_EXPIRED);
        } else if (!verifyCode.equals(registerRequest.getVerifyCode())) {
            throw new WrongVerifyCodeException(MessageConstant.WRONG_VERIFY_CODE);
        }

        String encryptPassword = passwordEncoder.encode(registerRequest.getPassword());

        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .password(encryptPassword)
                .username(registerRequest.getUsername())
                .build();

        userMapper.insertUser(newUser);

        Settings settings = Settings.builder()
                .userId(newUser.getId())
                .build();

        settingsMapper.insertSettings(settings);
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userMapper.selectUserByEmail(resetPasswordRequest.getEmail());

        if (user == null) {
            throw new UserNotExistsException(MessageConstant.USER_NOT_EXISTS);
        }

        String verifyCode = (String) redisTemplate.opsForValue().get(resetPasswordRequest.getEmail());
        if (verifyCode == null) {
            throw new VerifyCodeExpiredException(MessageConstant.VERIFY_CODE_EXPIRED);
        } else if (!verifyCode.equals(resetPasswordRequest.getVerifyCode())) {
            throw new WrongVerifyCodeException(MessageConstant.WRONG_VERIFY_CODE);
        }

        String encryptPassword = passwordEncoder.encode(resetPasswordRequest.getPassword());

        user.setPassword(encryptPassword);
        userMapper.updateUserById(user);
    }
}
