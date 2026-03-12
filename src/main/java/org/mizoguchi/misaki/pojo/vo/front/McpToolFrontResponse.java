package org.mizoguchi.misaki.pojo.vo.front;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpToolFrontResponse {
    private String name;

    private String description;
}