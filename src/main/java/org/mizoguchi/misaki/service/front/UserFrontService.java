package org.mizoguchi.misaki.service.front;

import org.mizoguchi.misaki.pojo.dto.front.UpdateSettingFrontRequest;
import org.mizoguchi.misaki.pojo.dto.front.UpdateUserFrontRequest;
import org.mizoguchi.misaki.pojo.vo.front.UserFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.SettingFrontResponse;

public interface UserFrontService {
    void checkIn(Long userId);
    void deleteAccount(Long userId);

    UserFrontResponse getUser(Long userId);
    void updateUser(Long userId, UpdateUserFrontRequest updateUserFrontRequest);

    SettingFrontResponse getSetting(Long userId);
    void updateSetting(Long userId, UpdateSettingFrontRequest updateSettingFrontRequest);

}
