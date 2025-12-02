package org.mizoguchi.misaki.entity.dto.common;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.MessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 2, max = 20)
    private String username;

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
