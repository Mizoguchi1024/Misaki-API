package org.mizoguchi.misaki.pojo.vo.front;

import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssistantFrontResponse {
    private Long id;

    private String name;

    private String personality;

    private Integer gender;

    @JsonFormat(pattern = JsonConstant.DATE_FORMAT)
    private LocalDate birthday;

    private String avatarPath;

    private Long modelId;

    private Long creatorId;

    private Integer likes;

    private Boolean likedFlag;

    private Integer duplicateName;

    private Boolean publicFlag;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime updateTime;

    @Version
    private Integer version;
}
