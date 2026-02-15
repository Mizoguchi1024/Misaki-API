package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RedisConstant;
import org.mizoguchi.misaki.common.exception.AlreadyCheckedInException;
import org.mizoguchi.misaki.common.exception.OptimisticLockFailedException;
import org.mizoguchi.misaki.pojo.entity.Settings;
import org.mizoguchi.misaki.pojo.dto.front.UpdateSettingFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateUserFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.UserFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.SettingFrontResponse;
import org.mizoguchi.misaki.mapper.SettingsMapper;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.service.common.FileService;
import org.mizoguchi.misaki.service.front.UserFrontService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserFrontServiceImpl implements UserFrontService {
    private final UserMapper userMapper;
    private final SettingsMapper settingsMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final FileService fileService;

    @Value("${misaki.business.user.check-in.token}")
    private Integer checkInTokenAmount;

    @Value("${misaki.business.user.check-in.crystal}")
    private Integer checkInCrystalAmount;

    @Value("${misaki.jwt.expiration}")
    private long jwtExpiration;

    @Override
    public void checkIn(Long userId) {
        User user = userMapper.selectById(userId);

        if (user.getLastCheckInDate() != null && user.getLastCheckInDate().equals(LocalDate.now())){
            throw new AlreadyCheckedInException(FailMessageConstant.ALREADY_CHECKED_IN);
        }

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getLastCheckInDate, LocalDate.now())
                .setIncrBy(User::getToken, checkInTokenAmount)
                .setIncrBy(User::getCrystal, checkInCrystalAmount)
                .setIncrBy(User::getVersion, 1)
        );
    }

    @Override
    public void deleteAccount(Long userId, String jwtId) {
        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getLastLoginTime, LocalDate.now())
                .set(User::getDeletePendingFlag, true)
                .setIncrBy(User::getVersion, 1)
        );

        redisTemplate.opsForValue().set(RedisConstant.BLOCKED_JWT + jwtId,
                String.valueOf(System.currentTimeMillis()),
                Duration.ofMillis(jwtExpiration));
    }

    @Override
    public UserFrontResponse getUser(Long userId) {
        User user = userMapper.selectById(userId);

        UserFrontResponse userFrontResponse = new UserFrontResponse();
        BeanUtils.copyProperties(user, userFrontResponse);

        return userFrontResponse;
    }

    @Override
    public void updateUser(Long userId, UpdateUserFrontRequest updateUserFrontRequest) {
        String originalAvatarPath = userMapper.selectById(userId).getAvatarPath();

        User user = new User();
        BeanUtils.copyProperties(updateUserFrontRequest, user);
        user.setId(userId);

        int affectedRows = userMapper.updateById(user);

        if (affectedRows == 0) {
            throw new OptimisticLockFailedException(FailMessageConstant.OPTIMISTIC_LOCK_FAILED);
        }

        if (StringUtils.hasText(originalAvatarPath) && StringUtils.hasText(updateUserFrontRequest.getAvatarPath())) {
            String fileName = new File(originalAvatarPath).getName();
            fileService.deleteFile(fileName);
        }
    }

    @Override
    public SettingFrontResponse getSetting(Long userId) {
        Settings settings = settingsMapper.selectOne(new LambdaQueryWrapper<Settings>()
                .eq(Settings::getUserId, userId)
        );

        SettingFrontResponse settingFrontResponse = new SettingFrontResponse();
        BeanUtils.copyProperties(settings, settingFrontResponse);

        return settingFrontResponse;
    }

    @Override
    public void updateSetting(Long userId, UpdateSettingFrontRequest updateSettingFrontRequest) {
        String originalBackgroundPath = settingsMapper.selectOne(new LambdaQueryWrapper<Settings>()
                .eq(Settings::getUserId, userId)
        ).getBackgroundPath();

        Settings settings = new Settings();
        BeanUtils.copyProperties(updateSettingFrontRequest, settings);
        settings.setUserId(userId);

        int affectedRows = settingsMapper.update(settings, new LambdaUpdateWrapper<Settings>()
                .eq(Settings::getUserId, userId)
        );

        if (affectedRows == 0) {
            throw new OptimisticLockFailedException(FailMessageConstant.OPTIMISTIC_LOCK_FAILED);
        }

        if (StringUtils.hasText(originalBackgroundPath) && StringUtils.hasText(updateSettingFrontRequest.getBackgroundPath())) {
            String fileName = new File(originalBackgroundPath).getName();
            fileService.deleteFile(fileName);
        }
    }
}
