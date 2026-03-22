package org.mizoguchi.misaki.service.common.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RedisConstant;
import org.mizoguchi.misaki.common.enumeration.AuthRoleEnum;
import org.mizoguchi.misaki.common.enumeration.GenderEnum;
import org.mizoguchi.misaki.common.exception.*;
import org.mizoguchi.misaki.common.util.JwtUtil;
import org.mizoguchi.misaki.mapper.AssistantMapper;
import org.mizoguchi.misaki.mapper.ModelUserMapper;
import org.mizoguchi.misaki.pojo.entity.Assistant;
import org.mizoguchi.misaki.pojo.entity.ModelUser;
import org.mizoguchi.misaki.pojo.entity.Settings;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.dto.common.LoginRequest;
import org.mizoguchi.misaki.pojo.dto.common.RegisterRequest;
import org.mizoguchi.misaki.pojo.dto.common.ResetPasswordRequest;
import org.mizoguchi.misaki.pojo.vo.common.LoginResponse;
import org.mizoguchi.misaki.mapper.SettingsMapper;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.service.common.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final SettingsMapper settingsMapper;
    private final AssistantMapper assistantMapper;
    private final ModelUserMapper modelUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${misaki.business.auth.max-password-retry-count}")
    private int maxPasswordRetryCount;

    @Value("${misaki.business.auth.password-retry-time-window}")
    private int passwordRetryTimeWindow;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String key = RedisConstant.PASSWORD_RETRY + loginRequest.getEmail();

        String value = redisTemplate.opsForValue().get(key);
        int retryCount = value == null ? 0 : Integer.parseInt(value);

        if (retryCount >= maxPasswordRetryCount) {
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
                redisTemplate.expire(key, Duration.ofMinutes(passwordRetryTimeWindow));
            }

            throw new WrongPasswordException(FailMessageConstant.WRONG_PASSWORD);
        }

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, user.getId())
                .set(User::getLastLoginTime, LocalDateTime.now())
                .set(User::getDeletePendingFlag, false)
                .setIncrBy(User::getVersion, 1));

        return LoginResponse.builder()
                .jwt(jwtUtil.generateToken(user.getId().toString(), user.getAuthRole()))
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

        String key = RedisConstant.EMAIL + registerRequest.getEmail();
        String verificationCode = redisTemplate.opsForValue().get(key);
        if (verificationCode == null) {
            throw new VerificationCodeExpiredException(FailMessageConstant.VERIFICATION_CODE_EXPIRED);
        } else if (!verificationCode.equals(registerRequest.getVerificationCode())) {
            throw new WrongVerificationCodeException(FailMessageConstant.WRONG_VERIFICATION_CODE);
        }
        redisTemplate.delete(key);

        String encryptPassword = passwordEncoder.encode(registerRequest.getPassword());

        User user = User.builder()
                .authRole(AuthRoleEnum.USER.getCode())
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(encryptPassword)
                .gender(GenderEnum.UNKNOWN.getCode())
                .token(100000)
                .crystal(0)
                .puzzle(0)
                .stardust(0)
                .deletePendingFlag(false)
                .build();

        userMapper.insert(user);

        modelUserMapper.insert(List.of(new ModelUser(null, user.getId(), 1L, null),
                new ModelUser(null, user.getId(), 2L, null)));

        Assistant assistantMisaki = Assistant.builder()
                .name("Misaki")
                .personality("可爱")
                .detail("拯救家里蹲男性的天使美少女。")
                .gender(GenderEnum.FEMALE.getCode())
                .birthday(LocalDate.of(2004, 1, 2))
                .modelId(1L)
                .creatorId(user.getId())
                .ownerId(user.getId())
                .publicFlag(false)
                .build();

        Assistant assistantHiyori = Assistant.builder()
                .name("Hiyori")
                .personality("治愈系")
                .detail("学校园艺部的成员，在家里也喜欢家庭农庄，喜欢的食物是小番茄。")
                .gender(GenderEnum.FEMALE.getCode())
                .birthday(LocalDate.of(2004, 2, 26))
                .modelId(2L)
                .creatorId(user.getId())
                .ownerId(user.getId())
                .publicFlag(false)
                .build();

        assistantMapper.insert(List.of(assistantMisaki, assistantHiyori));

        Settings settings = Settings.builder()
                .userId(user.getId())
                .mainColor("#3142ef")
                .promptsSuggestion(false)
                .ttsAutoplay(false)
                .backgroundOpacity(100)
                .backgroundBlur(0)
                .enabledAssistantId(assistantMisaki.getId())
                .build();

        settingsMapper.insert(settings);
        // TODO 插入别的必需的记录
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String key = RedisConstant.EMAIL + resetPasswordRequest.getEmail();
        String verificationCode = redisTemplate.opsForValue().get(key);

        if (verificationCode == null) {
            throw new VerificationCodeExpiredException(FailMessageConstant.VERIFICATION_CODE_EXPIRED);
        } else if (!verificationCode.equals(resetPasswordRequest.getVerificationCode())) {
            throw new WrongVerificationCodeException(FailMessageConstant.WRONG_VERIFICATION_CODE);
        }

        redisTemplate.delete(key);

        String encryptPassword = passwordEncoder.encode(resetPasswordRequest.getPassword());

        int affectedRows = userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getEmail, resetPasswordRequest.getEmail())
                .eq(User::getDeleteFlag, false)
                .set(User::getPassword, encryptPassword)
                .setIncrBy(User::getVersion, 1));

        if (affectedRows == 0) {
            throw new UserNotExistsException(FailMessageConstant.USER_NOT_EXISTS);
        }
    }
}
