package org.mizoguchi.misaki.service.front.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.mizoguchi.misaki.pojo.vo.front.McpServerFrontResponse;
import org.mizoguchi.misaki.service.front.McpFrontService;
import org.springframework.stereotype.Service;

import io.modelcontextprotocol.client.McpSyncClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class McpFrontServiceImpl implements McpFrontService {
    private final List<McpSyncClient> mcpSyncClients;

    @Override
    public List<McpServerFrontResponse> listMcpServers() {
        return mcpSyncClients.stream().map(mcpSyncClient -> {
            McpServerFrontResponse mcpServerFrontResponse = new McpServerFrontResponse();
            mcpServerFrontResponse.setName(mcpSyncClient.getServerInfo().name());
            List<McpServerFrontResponse.McpTool> tools = mcpSyncClient.listTools().tools().stream().map(tool -> {
                return new McpServerFrontResponse.McpTool(tool.name(), tool.description());
            }).collect(Collectors.toList());
            mcpServerFrontResponse.setTools(tools);

            return mcpServerFrontResponse;
        }).collect(Collectors.toList());
    }
}
