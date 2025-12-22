package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchUserAdminRequest {
    private Long id;

    private String email;

    private String username;

    private Integer gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent
    private LocalDate birthday;

    private String occupation;

    private String detail;

    private Integer authRole;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent
    private LocalDate lastCheckInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent
    private LocalDate lastLoginTime;

    private Boolean deletePendingFlag;

    private Boolean deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent
    private LocalDate createTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent
    private LocalDate updateTime;
}
