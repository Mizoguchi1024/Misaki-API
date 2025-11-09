package org.mizoguchi.misaki.service.impl;

import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.MessageConstant;
import org.mizoguchi.misaki.common.exception.*;
import org.mizoguchi.misaki.entity.Setting;
import org.mizoguchi.misaki.entity.dto.*;
import org.mizoguchi.misaki.mapper.SettingMapper;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.entity.User;
import org.mizoguchi.misaki.service.UserService;
import org.mizoguchi.misaki.common.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final SettingMapper settingMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public LoginDto login(LoginRequest loginRequest) {
        User user = userMapper.selectUserByEmail(loginRequest.getEmail());

        if (user == null) {
            throw new UserNotExistsException(MessageConstant.USER_NOT_EXISTS);
        } else if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new WrongPasswordException(MessageConstant.WRONG_PASSWORD);
        }

        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateUser(user);

        return LoginDto.builder()
                .token(jwtUtil.generateToken(user.getEmail(), user.getAuthRole()))
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

        String code = (String) redisTemplate.opsForValue().get(registerRequest.getEmail());
        if (code == null) {
            throw new VerifyCodeExpiredException(MessageConstant.VERIFY_CODE_EXPIRED);
        } else if (!code.equals(registerRequest.getCode())) {
            throw new WrongVerifyCodeException(MessageConstant.WRONG_VERIFY_CODE);
        }

        String encryptPassword = passwordEncoder.encode(registerRequest.getPassword());

        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .password(encryptPassword)
                .username(registerRequest.getUsername())
                .build();

        userMapper.insertUser(newUser);

        Setting setting = Setting.builder()
                .userId(newUser.getId())
                .build();

        settingMapper.insertSetting(setting);
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userMapper.selectUserByEmail(resetPasswordRequest.getEmail());

        if (user == null) {
            throw new UserNotExistsException(MessageConstant.USER_NOT_EXISTS);
        }

        String code = (String) redisTemplate.opsForValue().get(resetPasswordRequest.getEmail());
        if (code == null) {
            throw new VerifyCodeExpiredException(MessageConstant.VERIFY_CODE_EXPIRED);
        } else if (!code.equals(resetPasswordRequest.getCode())) {
            throw new WrongVerifyCodeException(MessageConstant.WRONG_VERIFY_CODE);
        }

        String encryptPassword = passwordEncoder.encode(resetPasswordRequest.getPassword());

        user.setPassword(encryptPassword);
        userMapper.updateUser(user);
    }

    @Override
    public void deleteUser(String email) {
        User user = User.builder()
                .email(email)
                .deleteFlag(1)
                .build();

        userMapper.updateUser(user);
    }

    @Override
    public User getProfile(String email) {
        return userMapper.selectUserByEmail(email);
    }

    @Override
    public void editProfile(String email, EditProfileRequest editProfileRequest) {
        User user = new User();
        user.setEmail(email);
        BeanUtils.copyProperties(editProfileRequest, user);

        userMapper.updateUser(user);
    }

    @Override
    public Setting getSetting(String email) {
        return settingMapper.selectSettingByUserId(userMapper.selectUserByEmail(email).getId());
    }

    @Override
    public void editSetting(String email, EditSettingRequest editSettingRequest) {
        Setting setting = new Setting();
        setting.setUserId(userMapper.selectUserByEmail(email).getId());
        BeanUtils.copyProperties(editSettingRequest, setting);

        settingMapper.updateSetting(setting);
    }
}
