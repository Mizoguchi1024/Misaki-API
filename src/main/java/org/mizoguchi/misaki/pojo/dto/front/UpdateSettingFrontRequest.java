package org.mizoguchi.misaki.pojo.dto.front;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;

@Data
public class UpdateSettingFrontRequest {
    @Min(0)
    @Max(2)
    private Integer appearance;

    @Min(0)
    @Max(2)
    private Integer language;

    private Boolean ttsAutoplay;

    @Pattern(regexp = RegexConstant.HEX_COLOR, message = FailMessageConstant.INVALID_FIELD_PATTERN)
    private String mainColor;

    @Min(0)
    @Max(16)
    private Integer borderRadius;

    @Pattern(regexp = RegexConstant.NOT_BLANK, message = FailMessageConstant.INVALID_FIELD_PATTERN)
    private String backgroundPath;

    private Long enabledAssistantId;

    @NotNull
    private Integer version;
}
