package org.mizoguchi.misaki.pojo.dto.front;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.MessageConstant;
import org.mizoguchi.misaki.common.constant.RegexConstant;

@Data
public class UpdateSettingFrontRequest {
    @NotNull
    @Min(0)
    @Max(2)
    private Integer appearance;

    @NotNull
    @Min(value = 0)
    @Max(value = 2)
    private Integer language;

    @NotNull
    @Min(value = 0)
    @Max(value = 1)
    private Integer ttsAutoplay;

    @NotNull
    @Min(value = 12)
    @Max(value = 24)
    private Integer fontSize;

    @NotNull
    @Pattern(regexp = RegexConstant.HEX_COLOR, message = MessageConstant.INVALID_FIELD_PATTEN)
    private String colorPrimary;

    @NotNull
    @Min(value = 1)
    @Max(value = 16)
    private Integer borderRadius;

    private String backgroundImagePath;

    private Long enabledAssistantId;
}
