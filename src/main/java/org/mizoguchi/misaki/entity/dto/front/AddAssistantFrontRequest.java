package org.mizoguchi.misaki.entity.dto.front;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddAssistantFrontRequest {
    private String name;

    private String personality;

    @Min(value = 0)
    @Max(value = 2)
    private Integer gender;

    @PastOrPresent
    private LocalDate birthday;

    private String avatarPath;

    private Long modelId;

    private Integer publicFlag;
}
