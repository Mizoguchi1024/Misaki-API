package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateUserAdminRequest {
    private String email;

    private String password;

    private String username;

    private Integer gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private String avatarPath;

    private String occupation;

    private String detail;

    private Integer authRole;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    private String token;

    private Integer crystal;

    private Integer puzzle;

    private Integer stardust;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastCheckInDate;

    private Boolean deleteFlag;
}
