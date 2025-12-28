package org.mizoguchi.misaki.pojo.dto.front;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateChatTitleFrontRequest {
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @NotNull
    private Integer version;
}
