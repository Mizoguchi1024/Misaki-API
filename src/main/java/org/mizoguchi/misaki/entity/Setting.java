package org.mizoguchi.misaki.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Setting {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer appearance;

    private Integer language;

    private Integer ttsAutoplay;

    private Integer fontSize;

    private String colorPrimary;

    private Integer borderRadius;

    private String backgroundImagePath;

    private Long enabledAssistantId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
