package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateUserAdminRequest {
    private String email;

    private String password;

    private String username;

    private Integer gender;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    private LocalDate birthday;

    private String avatarPath;

    private String occupation;

    private String detail;

    private Integer authRole;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime lastLoginTime;

    private String token;

    private Integer crystal;

    private Integer puzzle;

    private Integer stardust;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    private LocalDate lastCheckInDate;

    private Boolean deleteFlag;

    @NotNull
    private Integer version;
}
