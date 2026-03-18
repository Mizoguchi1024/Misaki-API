package org.mizoguchi.misaki.pojo.vo.admin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpServerAdminResponse {
    private String name;

    private String version;

    private List<McpToolAdminResponse> tools;
}
