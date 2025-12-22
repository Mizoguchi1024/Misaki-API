package org.mizoguchi.misaki.pojo.dto.admin;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateAssistantAdminRequest {
    private String name;

    private String personality;

    private Integer gender;

    private LocalDate birthday;

    private Long modelId;

    private Long creatorId;

    private Long ownerId;

    private Boolean publicFlag;

    private Boolean deleteFlag;

    @NotNull
    private Integer version;
}
