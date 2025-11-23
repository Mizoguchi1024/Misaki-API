package org.mizoguchi.misaki.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Assistant {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String personality;

    private Integer gender;

    private LocalDate birthday;

    private String avatarPath;

    private Long modelId;

    private Long creatorId;

    private Long ownerId;

    private Long moe;

    private Integer publicFlag;

    private Integer deleteFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
