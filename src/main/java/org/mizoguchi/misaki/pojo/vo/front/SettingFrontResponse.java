package org.mizoguchi.misaki.pojo.vo.front;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettingFrontResponse {
    private Integer appearance;

    private Integer language;

    private Integer ttsAutoplay;

    private Integer fontSize;

    private String colorPrimary;

    private Integer borderRadius;

    private String backgroundImagePath;
}
