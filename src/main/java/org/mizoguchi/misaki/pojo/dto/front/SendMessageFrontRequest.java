package org.mizoguchi.misaki.pojo.dto.front;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageFrontRequest {
    @NotBlank
    @Size(min = 1, max = 5000)
    private String content;

    @NotNull
    private String assistantId;

    @Size(min = 1, max = 100)
    private String prefix;
}
