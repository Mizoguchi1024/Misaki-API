package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Settings;
import org.mizoguchi.misaki.entity.User;
import org.mizoguchi.misaki.entity.dto.front.UpdateSettingFrontRequest;
import org.mizoguchi.misaki.entity.dto.front.UpdateUserFrontRequest;
import org.mizoguchi.misaki.entity.vo.front.UserFrontResponse;
import org.mizoguchi.misaki.entity.vo.front.SettingFrontResponse;

public interface UserService {
    void deleteAccount(Long userId);

    User getUserEntity(Long userId);
    UserFrontResponse getUserFrontResponse(Long userId);
    void updateUser(Long userId, UpdateUserFrontRequest updateUserFrontRequest);

    Settings getSettingEntity(Long userId);
    SettingFrontResponse getSettingFrontResponse(Long userId);
    void updateSetting(Long userId, UpdateSettingFrontRequest updateSettingFrontRequest);

}
