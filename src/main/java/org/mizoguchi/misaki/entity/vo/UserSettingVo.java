package org.mizoguchi.misaki.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSettingVo {
    private Integer appearance;

    private Integer language;

    private Integer ttsAutoplay;

    private Integer fontSize;

    private String colorPrimary;

    private Integer borderRadius;

    private String backgroundImageUrl;

    private String herName;

    private String personality;
}
