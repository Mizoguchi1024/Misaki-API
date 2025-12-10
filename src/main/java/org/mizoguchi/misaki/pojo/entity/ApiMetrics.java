package org.mizoguchi.misaki.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiMetrics {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String metricName;

    private String uri;

    private String method;

    private String status;

    private Long count;

    private Double totalTimeMs;

    private Double maxTimeMs;

    private LocalDateTime timestamp;
}
