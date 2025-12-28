package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.JsonConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;

import java.time.LocalDate;

@Data
public class AddAssistantAdminRequest {
    @NotBlank
    @Size(min = 1, max = 20)
    private String name;

    @Pattern(regexp = RegexConstant.NOT_BLANK, message = FailMessageConstant.INVALID_FIELD_PATTERN)
    @Size(min = 1, max = 20)
    private String personality;

    @Pattern(regexp = RegexConstant.NOT_BLANK, message = FailMessageConstant.INVALID_FIELD_PATTERN)
    @Size(min = 1, max = 100)
    private String detail;

    @NotNull
    @Min(0)
    @Max(2)
    private Integer gender;

    @PastOrPresent
    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    private LocalDate birthday;

    @NotNull
    @Min(0)
    private Long modelId;

    @NotNull
    @Min(0)
    private Long creatorId;

    @NotNull
    @Min(0)
    private Long ownerId;

    @NotNull
    private Boolean publicFlag;
}
