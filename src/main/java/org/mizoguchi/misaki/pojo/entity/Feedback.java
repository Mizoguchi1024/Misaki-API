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
public class Feedback {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long replierId;

    private Integer type;

    private String title;

    private String content;

    private String reply;

    private Integer status;

    @TableField(value = "delete_flag", fill =  FieldFill.INSERT)
    private Boolean deleteFlag;

    @TableField(fill =  FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    private Integer version;
}
