package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchChatAdminRequest {
    private Long id;

    private Long userId;

    private String title;

    private Boolean deleteFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
