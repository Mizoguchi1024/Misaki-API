package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

@Data
public class SearchFeedbackAdminRequest {
    private Long id;

    private Long userId;

    private Long replierId;

    private Integer type;

    private String title;

    private String content;

    private String reply;

    private Integer status;

    private Boolean deleteFlag;

    private String createTime;

    private String updateTime;
}
