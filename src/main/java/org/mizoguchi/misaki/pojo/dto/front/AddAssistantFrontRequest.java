package org.mizoguchi.misaki.pojo.dto.front;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDate;

@Data
public class AddAssistantFrontRequest {
    @NotBlank
    private String name;

    private String personality;

    private String detail;

    @Min(0)
    @Max(2)
    private Integer gender;

    @PastOrPresent
    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    private LocalDate birthday;

    @NotNull
    private Long modelId;

    @NotNull
    private Boolean publicFlag;
}
