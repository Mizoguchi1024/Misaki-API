package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.pojo.entity.Settings;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.dto.front.UpdateSettingFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateUserFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.UserFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.SettingFrontResponse;

public interface UserService {
    void deleteAccount(Long userId);

    User getUserEntity(Long userId);
    UserFrontResponse getUserFrontResponse(Long userId);
    void updateUser(Long userId, UpdateUserFrontRequest updateUserFrontRequest);

    Settings getSettingEntity(Long userId);
    SettingFrontResponse getSettingFrontResponse(Long userId);
    void updateSetting(Long userId, UpdateSettingFrontRequest updateSettingFrontRequest);

}
