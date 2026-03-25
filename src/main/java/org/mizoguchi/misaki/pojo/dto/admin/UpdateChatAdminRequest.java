package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class UpdateChatAdminRequest {
    @Pattern(regexp = RegexConstant.NOT_BLANK, message = FailMessageConstant.INVALID_FIELD_PATTERN)
    @Size(max = 50)
    private String title;

    private Boolean pinnedFlag;

    private Boolean deleteFlag;

    @NotNull
    private Integer version;
}
