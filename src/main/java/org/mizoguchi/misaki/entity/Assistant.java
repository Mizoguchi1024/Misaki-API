package org.mizoguchi.misaki.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDate;

public class Assistant {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String personality;

    private Integer gender;

    private LocalDate birthday;

    private String avatarUrl;
}
