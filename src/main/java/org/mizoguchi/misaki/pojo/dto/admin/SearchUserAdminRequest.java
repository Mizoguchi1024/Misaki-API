package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

@Data
public class SearchUserAdminRequest {
    private Long id;

    private Integer authRole;

    private String email;

    private String username;

    private Integer gender;

    private String birthday;

    private String occupation;

    private String details;

    private String lastCheckInDate;

    private String lastLoginTime;

    private Boolean deletePendingFlag;

    private Boolean deleteFlag;

    private String createTime;

    private String updateTime;
}
