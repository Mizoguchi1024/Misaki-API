package org.mizoguchi.misaki.pojo.dto.front;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDate;

@Data
public class UpdateUserFrontRequest {
    @NotBlank
    @Size(min = 2, max = 20)
    private String username;

    @NotNull
    @Min(value = 0)
    @Max(value = 2)
    private Integer gender;

    @PastOrPresent()
    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    private LocalDate birthday;

    private String avatarPath;

    @Size(max = 20)
    private String occupation;

    @Size(max = 255)
    private String detail;

    @NotNull
    private Integer version;
}
