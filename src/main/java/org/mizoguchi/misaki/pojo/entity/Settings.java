package org.mizoguchi.misaki.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settings {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String mainColor;

    private String backgroundPath;

    private Integer backgroundOpacity;

    private Integer backgroundBlur;

    @TableField("prompts_suggestion")
    private Boolean promptsSuggestion;

    @TableField("tts_autoplay")
    private Boolean ttsAutoplay;

    private Long enabledAssistantId;

    @TableField(fill =  FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    @TableField(fill =  FieldFill.INSERT)
    private Integer version;
}
