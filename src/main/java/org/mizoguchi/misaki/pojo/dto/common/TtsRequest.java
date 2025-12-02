package org.mizoguchi.misaki.pojo.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TtsRequest {
    @NotNull
    @NotBlank
    private String text;
}
