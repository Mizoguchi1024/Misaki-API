package org.mizoguchi.misaki.service.front;

import java.util.List;

import org.mizoguchi.misaki.pojo.vo.front.McpServerFrontResponse;

public interface McpFrontService {
    List<McpServerFrontResponse> listMcpServers();
}
