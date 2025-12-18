package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchModelAdminRequest {
    private Long id;

    private String name;

    private Integer grade;

    private Integer price;

    private String path;

    private String avatarPath;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
