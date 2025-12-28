package org.mizoguchi.misaki.pojo.dto.front;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddFeedbackFrontRequest {
    @NotNull
    @Min(0)
    @Max(10)
    private Integer type;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @NotBlank
    @Size(min = 1, max = 500)
    private String content;
}
