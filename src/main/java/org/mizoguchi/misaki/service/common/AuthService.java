package org.mizoguchi.misaki.service.common;

import org.mizoguchi.misaki.pojo.dto.common.LoginRequest;
import org.mizoguchi.misaki.pojo.dto.common.RegisterRequest;
import org.mizoguchi.misaki.pojo.dto.common.ResetPasswordRequest;
import org.mizoguchi.misaki.pojo.vo.common.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
