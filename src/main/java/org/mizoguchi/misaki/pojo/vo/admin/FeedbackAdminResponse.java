package org.mizoguchi.misaki.pojo.vo.admin;

import com.baomidou.mybatisplus.annotation.Version;
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
public class FeedbackAdminResponse {
    private Long id;

    private Long userId;

    private Long replierId;

    private Integer type;

    private String title;

    private String content;

    private String reply;

    private Integer status;

    private Boolean deleteFlag;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime updateTime;

    @Version
    private Integer version;
}
