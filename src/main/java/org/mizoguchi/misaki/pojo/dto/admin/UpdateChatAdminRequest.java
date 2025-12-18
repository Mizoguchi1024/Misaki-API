package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

@Data
public class UpdateChatAdminRequest {
    private String title;

    private Integer token;

    private Boolean deleteFlag;
}
