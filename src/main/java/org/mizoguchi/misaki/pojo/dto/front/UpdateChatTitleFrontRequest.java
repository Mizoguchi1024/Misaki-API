package org.mizoguchi.misaki.pojo.dto.front;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateChatTitleFrontRequest {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 20)
    private String title;
}
