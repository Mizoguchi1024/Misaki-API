package org.mizoguchi.misaki.pojo.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishAdminResponse {
    private Long id;

    private Long userId;

    private Boolean hitFlag;

    private Boolean duplicateFlag;

    private Long modelId;

    private Integer amount;

    private LocalDateTime createTime;
}
