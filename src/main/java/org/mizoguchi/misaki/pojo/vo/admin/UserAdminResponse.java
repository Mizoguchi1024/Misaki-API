package org.mizoguchi.misaki.pojo.vo.admin;

import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminResponse {
    private Long id;

    private String email;

    private String username;

    private Integer gender;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    private LocalDate birthday;

    private String avatarPath;

    private String occupation;

    private String details;

    private Integer authRole;

    private Integer token;

    private Integer crystal;

    private Integer puzzle;

    private Integer stardust;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime lastLoginTime;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    private LocalDate lastCheckInDate;

    private Boolean deletePendingFlag;

    private Boolean deleteFlag;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime updateTime;

    @Version
    private Integer version;
}
