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
public class MessageFrontResponse {
    private Long id;

    private Long chatId;

    private Long parentId;

    private String type;

    private String content;

    @JsonFormat(pattern = JsonConstant.DATE_TIME_FORMAT)
    private LocalDateTime createTime;
}
