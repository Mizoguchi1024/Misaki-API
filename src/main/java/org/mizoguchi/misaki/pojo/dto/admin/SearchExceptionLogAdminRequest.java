package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDate;

@Data
public class SearchExceptionLogAdminRequest {
    private Long id;

    private String exception;

    private String message;

    private String ip;

    private String uri;

    private String method;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    @PastOrPresent
    private LocalDate createTime;
}
