package org.mizoguchi.misaki.service;

import org.mizoguchi.misaki.entity.dto.common.LoginRequest;
import org.mizoguchi.misaki.entity.dto.common.RegisterRequest;
import org.mizoguchi.misaki.entity.dto.common.ResetPasswordRequest;
import org.mizoguchi.misaki.entity.vo.common.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
