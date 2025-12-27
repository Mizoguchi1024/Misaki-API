package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SearchAssistantAdminRequest {
    private Long id;

    private String name;

    private String personality;

    private Integer gender;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    private LocalDate birthday;

    private Long modelId;

    private Long creatorId;

    private Long ownerId;

    private Boolean publicFlag;

    private Boolean deleteFlag;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime updateTime;
}
