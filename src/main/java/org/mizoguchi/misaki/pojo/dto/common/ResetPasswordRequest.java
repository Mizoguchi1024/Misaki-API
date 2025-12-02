package org.mizoguchi.misaki.pojo.dto.common;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.MessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;

@Data
public class ResetPasswordRequest {
    @NotNull
    @Email()
    private String email;

    @NotBlank
    @Size(min = 6, max = 16)
    private String password;

    @NotBlank
    @Size(min = 6, max = 6)
    @Pattern(regexp = RegexConstant.PURE_NUMBER, message = MessageConstant.INVALID_FIELD_PATTEN)
    private String verifyCode;
}
