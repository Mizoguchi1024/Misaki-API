package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDate;

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

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    @PastOrPresent
    private LocalDate createTime;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    @PastOrPresent
    private LocalDate updateTime;
}
