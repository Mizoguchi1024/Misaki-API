package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

@Data
public class SearchModelAdminRequest {
    private Long id;

    private String name;

    private Integer grade;

    private Integer price;

    private String path;

    private Boolean onSaleFlag;

    private String createTime;

    private String updateTime;
}
