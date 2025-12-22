package org.mizoguchi.misaki.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assistant {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private String personality;

    private Integer gender;

    private LocalDate birthday;

    private Long modelId;

    private Long creatorId;

    private Long ownerId;

    @TableField(value = "public_flag")
    private Boolean publicFlag;

    @TableField(value = "delete_flag", fill =  FieldFill.INSERT)
    private Boolean deleteFlag;

    @TableField(fill =  FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    private Integer version;
}
