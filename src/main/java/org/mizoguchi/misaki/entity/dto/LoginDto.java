package org.mizoguchi.misaki.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDto {
    private String token;

    private Integer authRole;
}
