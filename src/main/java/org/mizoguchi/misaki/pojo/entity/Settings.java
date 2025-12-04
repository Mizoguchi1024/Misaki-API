package org.mizoguchi.misaki.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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

    private Integer appearance;

    private Integer language;

    private String colorPrimary;

    private Integer borderRadius;

    private String backgroundPath;

    @TableField("tts_autoplay")
    private Boolean ttsAutoplay;

    private Long enabledAssistantId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
