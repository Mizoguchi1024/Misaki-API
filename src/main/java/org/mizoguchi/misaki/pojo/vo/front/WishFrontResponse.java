package org.mizoguchi.misaki.pojo.vo.front;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mizoguchi.misaki.common.constant.JsonConstant;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishFrontResponse {
    private Long id;

    private Boolean hitFlag;

    private Boolean duplicateFlag;

    private Long modelId;

    private String modelName;

    private Integer modelGrade;

    private String modelAvatarPath;

    private Integer amount;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime createTime;
}
