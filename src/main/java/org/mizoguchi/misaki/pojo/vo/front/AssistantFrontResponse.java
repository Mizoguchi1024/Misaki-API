package org.mizoguchi.misaki.pojo.vo.front;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssistantFrontResponse {
    private Long id;

    private String name;

    private String personality;

    private Integer gender;

    private LocalDate birthday;

    private String avatarPath;

    private Long modelId;

    private Long creatorId;

    private Long moe;

    private Integer publicFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
