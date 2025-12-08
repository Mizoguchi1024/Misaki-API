package org.mizoguchi.misaki.pojo.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddAssistantAdminRequest {
    private String name;

    private String personality;

    private Integer gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private String modelId;

    private String creatorId;

    private String ownerId;

    private Boolean publicFlag;
}
