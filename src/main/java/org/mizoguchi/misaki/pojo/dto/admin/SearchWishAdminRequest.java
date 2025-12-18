package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchWishAdminRequest {
    private Long id;

    private Long userId;

    private Boolean hitFlag;

    private Boolean duplicateFlag;

    private Long modelId;

    private Integer amount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
