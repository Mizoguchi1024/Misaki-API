package org.mizoguchi.misaki.pojo.vo.front;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingFrontResponse {
    private Integer appearance;

    private Integer language;

    private Integer ttsAutoplay;

    private String mainColor;

    private Integer borderRadius;

    private String backgroundPath;
}
