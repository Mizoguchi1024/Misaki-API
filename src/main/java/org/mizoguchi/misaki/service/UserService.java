package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Setting;
import org.mizoguchi.misaki.entity.User;
import org.mizoguchi.misaki.entity.dto.*;
import org.mizoguchi.misaki.entity.dto.front.UpdateSettingFrontRequest;
import org.mizoguchi.misaki.entity.dto.front.UpdateUserFrontRequest;
import org.mizoguchi.misaki.entity.vo.LoginResponse;
import org.mizoguchi.misaki.entity.vo.front.UserFrontResponse;
import org.mizoguchi.misaki.entity.vo.front.SettingFrontResponse;

public interface UserService {
    LoginResponse login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
    void deleteAccount(Long userId);

    User getUserEntity(Long userId);
    UserFrontResponse getUserFrontResponse(Long userId);
    void updateUser(Long userId, UpdateUserFrontRequest updateUserFrontRequest);

    Setting getSettingEntity(Long userId);
    SettingFrontResponse getSettingFrontResponse(Long userId);
    void updateSetting(Long userId, UpdateSettingFrontRequest updateSettingFrontRequest);

}
