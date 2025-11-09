package org.mizoguchi.misaki.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotNull
    @Email()
    private String email;

    @NotBlank
    @Size(min = 6, max = 16)
    private String password;
}
