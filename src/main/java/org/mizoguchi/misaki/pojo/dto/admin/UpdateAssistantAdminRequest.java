package org.mizoguchi.misaki.pojo.dto.admin;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;

import java.time.LocalDate;

@Data
public class UpdateAssistantAdminRequest {
    @Pattern(regexp = RegexConstant.NOT_BLANK, message = FailMessageConstant.INVALID_FIELD_PATTERN)
    @Size(min = 1, max = 20)
    private String name;

    private String personality;

    private String details;

    private Integer gender;

    private LocalDate birthday;

    private Long modelId;

    private Long creatorId;

    private Long ownerId;

    private Boolean publicFlag;

    private Boolean deleteFlag;

    @NotNull
    private Integer version;
}
