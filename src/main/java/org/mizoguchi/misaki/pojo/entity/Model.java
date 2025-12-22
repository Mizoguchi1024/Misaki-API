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
public class Model {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private Integer grade;

    private Integer price;

    private String path;

    private String avatarPath;

    @TableField("on_sale_flag")
    private Boolean onSaleFlag;

    @TableField(fill =  FieldFill.INSERT)
    private LocalDateTime createTime;

    @Version
    private Integer version;
}
