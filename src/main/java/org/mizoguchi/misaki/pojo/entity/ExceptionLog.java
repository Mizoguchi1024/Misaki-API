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
public class ExceptionLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String exception;

    private String message;

    private String ip;

    private String uri;

    private String method;

    @TableField(fill =  FieldFill.INSERT)
    private LocalDateTime createTime;
}
