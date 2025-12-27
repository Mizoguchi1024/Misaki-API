package org.mizoguchi.misaki.pojo.dto.front;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDate;

@Data
public class AddAssistantFrontRequest {
    private String name;

    private String personality;

    @Min(value = 0)
    @Max(value = 2)
    private Integer gender;

    @PastOrPresent
    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    private LocalDate birthday;

    private String avatarPath;

    private Long modelId;

    private Boolean publicFlag;
}
