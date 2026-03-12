package org.mizoguchi.misaki.pojo.vo.front;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpServerFrontResponse {
    private String name;

    private String version;

    private List<McpToolFrontResponse> tools;
}