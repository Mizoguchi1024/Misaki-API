package org.mizoguchi.misaki.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.MessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;

import java.time.LocalDate;

@Data
public class EditProfileRequest {
    @NotBlank
    @Size(min = 2, max = 20)
    private String username;

    @NotNull
    @Min(value = 0)
    @Max(value = 2)
    private Integer gender;

    @PastOrPresent()
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private String avatarUrl;

    @Size(max = 20)
    private String occupation;

    @Size(max = 255)
    private String detail;
}
