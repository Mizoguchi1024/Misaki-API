package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.pojo.dto.front.UpdateSettingFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateUserFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.UserFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.SettingFrontResponse;

public interface UserService {
    void checkIn(Long userId);
    void deleteAccount(Long userId);

    UserFrontResponse getUserFrontResponse(Long userId);
    void updateUser(Long userId, UpdateUserFrontRequest updateUserFrontRequest);

    SettingFrontResponse getSettingFrontResponse(Long userId);
    void updateSetting(Long userId, UpdateSettingFrontRequest updateSettingFrontRequest);

}
