package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDate;

@Data
public class SearchAssistantAdminRequest {
    private Long id;

    private String name;

    private String personality;

    private String details;

    private Integer gender;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    @PastOrPresent
    private LocalDate birthday;

    private Long modelId;

    private Long creatorId;

    private Long ownerId;

    private Boolean publicFlag;

    private Boolean deleteFlag;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    @PastOrPresent
    private LocalDate createTime;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    @PastOrPresent
    private LocalDate updateTime;
}
