package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDateTime;

@Data
public class SearchFeedbackAdminRequest {
    private Long id;

    private Long userId;

    private Long replierId;

    private Integer type;

    private String title;

    private String content;

    private String reply;

    private Integer status;

    private Boolean deleteFlag;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime updateTime;
}
