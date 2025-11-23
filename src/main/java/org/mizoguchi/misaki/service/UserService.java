package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Setting;
import org.mizoguchi.misaki.entity.User;
import org.mizoguchi.misaki.entity.dto.*;

public interface UserService {
    LoginDto login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
    User getProfile(Long userId);
    void editProfile(Long userId, EditProfileRequest editProfileRequest);
    Setting getSetting(Long userId);
    void editSetting(Long userId, EditSettingRequest editSettingRequest);
    void deleteUser(Long userId);
}
