package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.dto.front.UpdateSettingFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateUserFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.UserFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.SettingFrontResponse;

public interface UserFrontService {
    UserFrontResponse getUser(Long userId);
    void updateUser(Long userId, UpdateUserFrontRequest updateUserFrontRequest);
    void checkIn(Long userId);
    void deleteAccount(Long userId, String jwtId);

    SettingFrontResponse getSetting(Long userId);
    void updateSetting(Long userId, UpdateSettingFrontRequest updateSettingFrontRequest);

}
