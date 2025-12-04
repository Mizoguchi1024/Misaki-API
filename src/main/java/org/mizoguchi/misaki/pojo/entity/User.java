package org.mizoguchi.misaki.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
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
    @TableId(type = IdType.AUTO)
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

    private LocalDateTime lastLoginTime;

    private Long token;

    private Integer crystal;

    private Integer puzzle;

    private Integer stardust;

    private LocalDate lastCheckInTime;

    @TableField("delete_flag")
    private Boolean deleteFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
