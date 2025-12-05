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
public class Wish {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    @TableField("hit_flag")
    private Boolean hitFlag;

    @TableField("duplicate_flag")
    private Boolean duplicateFlag;

    private Long modelId;

    private Integer amount;

    private LocalDateTime createTime;
}
