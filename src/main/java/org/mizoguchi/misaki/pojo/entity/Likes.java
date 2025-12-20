package org.mizoguchi.misaki.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
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
public class Likes {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Integer targetType;

    private Long targetId;

    @TableField(fill =  FieldFill.INSERT)
    private LocalDateTime createTime;
}
