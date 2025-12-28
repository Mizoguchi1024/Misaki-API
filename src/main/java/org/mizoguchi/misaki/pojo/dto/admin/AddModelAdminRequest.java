package org.mizoguchi.misaki.pojo.dto.admin;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;

@Data
public class AddModelAdminRequest {
    @NotBlank
    @Size(min = 1, max = 20)
    private String name;

    @NotNull
    @Min(0)
    @Max(1)
    private Integer grade;

    @Min(0)
    private Integer price;

    @NotBlank
    private String path;

    @Pattern(regexp = RegexConstant.NOT_BLANK, message = FailMessageConstant.INVALID_FIELD_PATTERN)
    private String avatarPath;

    @NotNull
    private Boolean onSaleFlag;
}
