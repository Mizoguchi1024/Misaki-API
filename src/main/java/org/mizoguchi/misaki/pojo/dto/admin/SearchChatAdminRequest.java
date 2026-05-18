package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

@Data
public class SearchChatAdminRequest {
    private Long id;

    private Long userId;

    private String title;

    private Boolean pinnedFlag;

    private Boolean deleteFlag;

    private String createTime;

    private String updateTime;
}
