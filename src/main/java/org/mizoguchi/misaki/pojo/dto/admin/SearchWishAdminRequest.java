package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

@Data
public class SearchWishAdminRequest {
    private Long id;

    private Long userId;

    private Boolean hitFlag;

    private Boolean duplicateFlag;

    private Long modelId;

    private Integer amount;

    private String createTime;
}
