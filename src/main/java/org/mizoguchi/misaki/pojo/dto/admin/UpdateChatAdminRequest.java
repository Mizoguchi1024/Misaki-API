package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class UpdateChatAdminRequest {
    private String title;

    private Integer token;

    private Boolean deleteFlag;

    @NotNull
    private Integer version;
}
