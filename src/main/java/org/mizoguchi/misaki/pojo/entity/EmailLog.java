package org.mizoguchi.misaki.pojo.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String sender;

    private String receiver;

    private String subject;

    private String createTime;
}
