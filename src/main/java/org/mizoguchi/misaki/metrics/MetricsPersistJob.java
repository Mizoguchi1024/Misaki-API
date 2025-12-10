package org.mizoguchi.misaki.metrics;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.mapper.ApiMetricsMapper;
import org.mizoguchi.misaki.pojo.entity.ApiMetrics;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsPersistJob {

    private final MeterRegistry meterRegistry;
    private final ApiMetricsMapper apiMetricsMapper;

    @Scheduled(fixedRate = 3600000) // 每小时执行
    public void persist() {
        for (Meter meter : meterRegistry.getMeters()) {

            if (!(meter instanceof Timer timer)) {
                continue;
            }

            String uri = null;
            String method = null;
            String status = null;

            for (Tag tag : meter.getId().getTags()) {
                switch (tag.getKey()) {
                    case "uri" -> uri = tag.getValue();
                    case "method" -> method = tag.getValue();
                    case "status" -> status = tag.getValue();
                }
            }

            ApiMetrics apiMetrics = ApiMetrics.builder()
                    .metricName(meter.getId().getName())
                    .uri(uri)
                    .method(method)
                    .status(status)
                    .count(timer.count())
                    .totalTimeMs(timer.totalTime(TimeUnit.MILLISECONDS))
                    .maxTimeMs(timer.max(TimeUnit.MILLISECONDS))
                    .timestamp(LocalDateTime.now())
                    .build();

            apiMetricsMapper.insert(apiMetrics);
        }
    }
}