package org.mizoguchi.misaki.pojo.dto.front;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;

@Data
public class UpdateSettingFrontRequest {
    private Boolean ttsAutoplay;

    @Pattern(regexp = RegexConstant.HEX_COLOR, message = FailMessageConstant.INVALID_FIELD_PATTERN)
    private String mainColor;

    private String backgroundPath;

    private Long enabledAssistantId;

    @NotNull
    private Integer version;
}
