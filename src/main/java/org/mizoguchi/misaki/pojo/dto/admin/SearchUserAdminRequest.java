package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDate;

@Data
public class SearchUserAdminRequest {
    private Long id;

    private String email;

    private String username;

    private Integer gender;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    @PastOrPresent
    private LocalDate birthday;

    private String occupation;

    private String detail;

    private Integer authRole;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    @PastOrPresent
    private LocalDate lastCheckInDate;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    @PastOrPresent
    private LocalDate lastLoginTime;

    private Boolean deletePendingFlag;

    private Boolean deleteFlag;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    @PastOrPresent
    private LocalDate createTime;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    @PastOrPresent
    private LocalDate updateTime;
}
