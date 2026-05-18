package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

@Data
public class SearchAssistantAdminRequest {
    private Long id;

    private String name;

    private String personality;

    private String details;

    private Integer gender;

    private String birthday;

    private Long modelId;

    private Long creatorId;

    private Long ownerId;

    private Boolean publicFlag;

    private Boolean deleteFlag;

    private String createTime;

    private String updateTime;
}
