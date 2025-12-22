package org.mizoguchi.misaki.pojo.vo.admin;

import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelAdminResponse {
    private Long id;

    private String name;

    private Integer grade;

    private Integer price;

    private String path;

    private String avatarPath;

    private Boolean onSaleFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Version
    private Integer version;
}
