package org.mizoguchi.misaki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.SystemConstant;
import org.mizoguchi.misaki.common.exception.AlreadyCheckInException;
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
        User user = userMapper.selectById(userId);

        if (user.getLastCheckInTime() != null && user.getLastCheckInTime().equals(LocalDate.now())){
            throw new AlreadyCheckInException(FailMessageConstant.ALREADY_CHECK_IN);
        }

        user.setLastCheckInTime(LocalDate.now());
        user.setToken(user.getToken() + SystemConstant.CHECK_IN_TOKEN_AMOUNT);
        user.setCrystal(user.getCrystal() + SystemConstant.CHECK_IN_CRYSTAL_AMOUNT);
        userMapper.updateById(user);
    }

    @Override
    public void deleteAccount(Long userId) {
        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getDeleteFlag, true));
    }

    @Override
    public UserFrontResponse getUserFrontResponse(Long userId) {
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
    public SettingFrontResponse getSettingFrontResponse(Long userId) {
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
