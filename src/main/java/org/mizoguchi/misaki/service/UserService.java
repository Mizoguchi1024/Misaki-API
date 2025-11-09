package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.Setting;
import org.mizoguchi.misaki.entity.User;
import org.mizoguchi.misaki.entity.dto.*;

public interface UserService {
    LoginDto login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
    User getProfile(String email);
    void editProfile(String email, EditProfileRequest editProfileRequest);
    Setting getSetting(String email);
    void editSetting(String email, EditSettingRequest editSettingRequest);
    void deleteUser(String email);
}
