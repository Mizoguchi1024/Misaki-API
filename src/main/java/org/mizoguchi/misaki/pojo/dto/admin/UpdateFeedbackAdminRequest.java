package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class UpdateFeedbackAdminRequest {
    private Long replierId;

    private Integer type;

    private String title;

    private String content;

    private String reply;

    private Integer status;

    private Boolean deleteFlag;

    @NotNull
    private Integer version;
}
