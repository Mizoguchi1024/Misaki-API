package org.mizoguchi.misaki.pojo.vo.front;

import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingFrontResponse {
    private Boolean promptsSuggestion;

    private Boolean ttsAutoplay;

    private String mainColor;

    private String backgroundPath;

    private Integer backgroundOpacity;

    private Integer backgroundBlur;

    private Long enabledAssistantId;

    @Version
    private Integer version;
}
