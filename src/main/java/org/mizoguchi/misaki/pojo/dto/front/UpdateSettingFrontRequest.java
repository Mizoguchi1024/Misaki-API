package org.mizoguchi.misaki.pojo.dto.front;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
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
    private Boolean ttsAutoplay;

    @NotNull
    @Pattern(regexp = RegexConstant.HEX_COLOR, message = FailMessageConstant.INVALID_FIELD_PATTEN)
    private String colorPrimary;

    @NotNull
    @Min(value = 1)
    @Max(value = 16)
    private Integer borderRadius;

    private String backgroundPath;

    private Long enabledAssistantId;
}
