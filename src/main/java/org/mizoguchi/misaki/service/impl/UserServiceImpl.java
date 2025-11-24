package org.mizoguchi.misaki.service.impl;

import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.MessageConstant;
import org.mizoguchi.misaki.common.exception.*;
import org.mizoguchi.misaki.entity.Setting;
import org.mizoguchi.misaki.entity.dto.*;
import org.mizoguchi.misaki.entity.vo.LoginResponse;
import org.mizoguchi.misaki.entity.vo.UserProfileResponse;
import org.mizoguchi.misaki.entity.vo.UserSettingResponse;
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
        userMapper.updateUserById(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = User.builder()
                .id(userId)
                .deleteFlag(1)
                .build();

        userMapper.updateUserById(user);
    }

    @Override
    public User getUserEntity(Long userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = getUserEntity(userId);

        UserProfileResponse userProfileResponse = new UserProfileResponse();
        BeanUtils.copyProperties(user, userProfileResponse);

        return userProfileResponse;
    }

    @Override
    public void editProfile(Long userId, EditProfileRequest editProfileRequest) {
        User user = new User();
        user.setId(userId);
        BeanUtils.copyProperties(editProfileRequest, user);

        userMapper.updateUserById(user);
    }

    @Override
    public Setting getSettingEntity(Long userId) {
        return settingMapper.selectSettingByUserId(userId);
    }

    @Override
    public UserSettingResponse getSetting(Long userId) {
        Setting setting = getSettingEntity(userId);

        UserSettingResponse userSettingResponse = new UserSettingResponse();
        BeanUtils.copyProperties(setting, userSettingResponse);

        return userSettingResponse;
    }

    @Override
    public void editSetting(Long userId, EditSettingRequest editSettingRequest) {
        Setting setting = new Setting();
        setting.setUserId(userId);
        BeanUtils.copyProperties(editSettingRequest, setting);

        settingMapper.updateSettingByUserId(setting);
    }
}
