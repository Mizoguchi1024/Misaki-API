package org.mizoguchi.misaki.service.impl;

import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.SystemConstant;
import org.mizoguchi.misaki.common.exception.TodayAlreadyCheckInException;
import org.mizoguchi.misaki.pojo.entity.Settings;
import org.mizoguchi.misaki.pojo.dto.front.UpdateSettingFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateUserFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.UserFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.SettingFrontResponse;
import org.mizoguchi.misaki.mapper.SettingsMapper;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final SettingsMapper settingsMapper;

    @Override
    public void checkIn(Long userId) {
        User user = userMapper.selectUserById(userId);
        if (user.getLastCheckInTime() == null || !user.getLastCheckInTime().equals(LocalDate.now())) {
            user.setLastCheckInTime(LocalDate.now());
            user.setToken(user.getToken() + SystemConstant.CHECK_IN_TOKEN_AMOUNT);
            user.setCrystal(user.getCrystal() + SystemConstant.CHECK_IN_CRYSTAL_AMOUNT);
            userMapper.updateUserById(user);
        }else {
            throw new TodayAlreadyCheckInException(FailMessageConstant.TODAY_ALREADY_CHECK_IN);
        }
    }

    @Override
    public void deleteAccount(Long userId) {
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
    public UserFrontResponse getUserFrontResponse(Long userId) {
        User user = getUserEntity(userId);

        UserFrontResponse userFrontResponse = new UserFrontResponse();
        BeanUtils.copyProperties(user, userFrontResponse);

        return userFrontResponse;
    }

    @Override
    public void updateUser(Long userId, UpdateUserFrontRequest updateUserFrontRequest) {
        User user = new User();
        user.setId(userId);
        BeanUtils.copyProperties(updateUserFrontRequest, user);

        userMapper.updateUserById(user);
    }

    @Override
    public Settings getSettingEntity(Long userId) {
        return settingsMapper.selectSettingsByUserId(userId);
    }

    @Override
    public SettingFrontResponse getSettingFrontResponse(Long userId) {
        Settings settings = getSettingEntity(userId);

        SettingFrontResponse settingFrontResponse = new SettingFrontResponse();
        BeanUtils.copyProperties(settings, settingFrontResponse);

        return settingFrontResponse;
    }

    @Override
    public void updateSetting(Long userId, UpdateSettingFrontRequest updateSettingFrontRequest) {
        Settings settings = new Settings();
        settings.setUserId(userId);
        BeanUtils.copyProperties(updateSettingFrontRequest, settings);

        settingsMapper.updateSettingsByUserId(settings);
    }
}
