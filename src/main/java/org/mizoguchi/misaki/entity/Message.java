package org.mizoguchi.misaki.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long chatId;

    private String type;

    private String content;

    private Integer tokens;

    private Integer mcpEnabled;

    private Integer deleteFlag;

    private LocalDateTime timestamp;
}
