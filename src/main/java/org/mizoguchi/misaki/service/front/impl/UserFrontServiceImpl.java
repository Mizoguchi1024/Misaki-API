package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.BusinessConstant;
import org.mizoguchi.misaki.common.exception.AlreadyCheckedInException;
import org.mizoguchi.misaki.pojo.entity.Settings;
import org.mizoguchi.misaki.pojo.dto.front.UpdateSettingFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateUserFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.UserFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.SettingFrontResponse;
import org.mizoguchi.misaki.mapper.SettingsMapper;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.service.front.UserFrontService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserFrontServiceImpl implements UserFrontService {
    private final UserMapper userMapper;
    private final SettingsMapper settingsMapper;

    @Override
    public void checkIn(Long userId) {
        User user = userMapper.selectById(userId);

        if (user.getLastCheckInDate() != null && user.getLastCheckInDate().equals(LocalDate.now())){
            throw new AlreadyCheckedInException(FailMessageConstant.ALREADY_CHECKED_IN);
        }

        user.setLastCheckInDate(LocalDate.now());
        user.setToken(user.getToken() + BusinessConstant.CHECK_IN_TOKEN_AMOUNT);
        user.setCrystal(user.getCrystal() + BusinessConstant.CHECK_IN_CRYSTAL_AMOUNT);
        userMapper.updateById(user);
    }

    @Override
    public void deleteAccount(Long userId) {
        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getLastLoginTime, LocalDate.now())
                .set(User::getDeletePendingFlag, true));
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
        User user = new User();
        user.setId(userId);
        BeanUtils.copyProperties(updateUserFrontRequest, user);

        userMapper.updateById(user);
    }

    @Override
    public SettingFrontResponse getSetting(Long userId) {
        Settings settings = settingsMapper.selectOne(new LambdaQueryWrapper<Settings>().eq(Settings::getUserId, userId));

        SettingFrontResponse settingFrontResponse = new SettingFrontResponse();
        BeanUtils.copyProperties(settings, settingFrontResponse);

        return settingFrontResponse;
    }

    @Override
    public void updateSetting(Long userId, UpdateSettingFrontRequest updateSettingFrontRequest) {
        Settings settings = new Settings();
        settings.setUserId(userId);
        BeanUtils.copyProperties(updateSettingFrontRequest, settings);

        settingsMapper.update(settings, new LambdaUpdateWrapper<Settings>().eq(Settings::getUserId, userId));
    }
}
