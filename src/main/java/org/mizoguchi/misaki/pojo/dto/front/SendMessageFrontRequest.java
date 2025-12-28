package org.mizoguchi.misaki.pojo.dto.front;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageFrontRequest {
    @Min(0)
    private Long parentId;

    @Size(min = 1, max = 100)
    private String prefix;

    @NotBlank
    @Size(min = 1, max = 5000)
    private String content;
}
