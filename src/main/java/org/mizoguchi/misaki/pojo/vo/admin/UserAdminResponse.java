package org.mizoguchi.misaki.pojo.vo.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminResponse {
    private Long id;

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

    private Long token;

    private Integer crystal;

    private Integer puzzle;

    private Integer stardust;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastCheckInDate;

    private Boolean deletePendingFlag;

    private Boolean deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
