package org.mizoguchi.misaki.pojo.vo.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionLogAdminResponse {
    private Long id;

    private String exception;

    private String message;

    private String ip;

    private String uri;

    private String method;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime createTime;
}
