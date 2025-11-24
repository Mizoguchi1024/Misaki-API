package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Setting;
import org.mizoguchi.misaki.entity.User;
import org.mizoguchi.misaki.entity.dto.*;
import org.mizoguchi.misaki.entity.vo.LoginResponse;
import org.mizoguchi.misaki.entity.vo.UserProfileResponse;
import org.mizoguchi.misaki.entity.vo.UserSettingResponse;

public interface UserService {
    LoginResponse login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
    User getUserEntity(Long userId);
    UserProfileResponse getProfile(Long userId);
    void editProfile(Long userId, EditProfileRequest editProfileRequest);
    Setting getSettingEntity(Long userId);
    UserSettingResponse getSetting(Long userId);
    void editSetting(Long userId, EditSettingRequest editSettingRequest);
    void deleteUser(Long userId);
}
