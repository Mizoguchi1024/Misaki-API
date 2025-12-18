package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

@Data
public class UpdateModelAdminRequest {
    private String name;

    private Integer grade;

    private Integer price;

    private String path;

    private String avatarPath;
}
