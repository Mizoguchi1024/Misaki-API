package org.mizoguchi.misaki.pojo.dto.front;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ListPromptsFrontRequest {
    @NotNull
    private Long parentId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer size;
}
