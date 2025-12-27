package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDateTime;

@Data
public class SearchEmailLogAdminRequest {
    private Long id;

    private String sender;

    private String receiver;

    private String subject;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime createTime;
}
