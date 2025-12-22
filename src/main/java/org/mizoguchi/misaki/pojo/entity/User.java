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
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String email;

    private String password;

    private String username;

    private Integer gender;

    private LocalDate birthday;

    private String avatarPath;

    private String occupation;

    private String detail;

    private Integer authRole;

    private Integer token;

    private Integer crystal;

    private Integer puzzle;

    private Integer stardust;

    private LocalDate lastCheckInDate;

    private LocalDateTime lastLoginTime;

    @TableField(value = "delete_pending_flag")
    private Boolean deletePendingFlag;

    @TableField(value = "delete_flag", fill =  FieldFill.INSERT)
    private Boolean deleteFlag;

    @TableField(fill =  FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    private Integer version;
}
