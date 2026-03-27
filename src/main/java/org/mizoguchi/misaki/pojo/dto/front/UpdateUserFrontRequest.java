package org.mizoguchi.misaki.pojo.dto.front;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.JsonConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;

import java.time.LocalDate;

@Data
public class UpdateUserFrontRequest {
    @Pattern(regexp = RegexConstant.NOT_BLANK, message = FailMessageConstant.INVALID_FIELD_PATTERN)
    @Size(min = 1, max = 20)
    private String username;

    @Min(0)
    @Max(2)
    private Integer gender;

    @PastOrPresent()
    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    private LocalDate birthday;

    private String avatarPath;

    @Size(max = 20)
    private String occupation;

    @Size(max = 255)
    private String details;

    @NotNull
    private Integer version;
}
