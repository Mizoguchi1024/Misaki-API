package org.mizoguchi.misaki.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotBlank
    @Size(min = 1, max = 5000)
    private String content;

    @NotNull
    private Long assistantId;

    @Size(min = 1, max = 100)
    private String prefix;
}
