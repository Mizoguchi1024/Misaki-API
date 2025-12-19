package org.mizoguchi.misaki.pojo.entity;

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
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long chatId;

    private Long parentId;

    private String type;

    private String content;

    private LocalDateTime createTime;
}
