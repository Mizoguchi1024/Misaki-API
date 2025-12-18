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
public class ChatAdminResponse {
    private Long id;

    private Long userId;

    private String title;

    private Integer token;

    private Boolean deleteFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
