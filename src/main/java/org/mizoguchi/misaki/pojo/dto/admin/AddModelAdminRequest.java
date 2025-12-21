package org.mizoguchi.misaki.pojo.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddModelAdminRequest {
    private String name;

    private Integer grade;

    private Integer price;

    @NotNull
    private String path;

    private String avatarPath;

    @NotNull
    private Boolean onSaleFlag;
}
