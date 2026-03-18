package org.mizoguchi.misaki.service.admin.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.mizoguchi.misaki.pojo.vo.admin.McpServerAdminResponse;
import org.mizoguchi.misaki.pojo.vo.admin.McpToolAdminResponse;
import org.mizoguchi.misaki.service.admin.McpAdminService;
import org.springframework.stereotype.Service;

import io.modelcontextprotocol.client.McpSyncClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class McpAdminServiceImpl implements McpAdminService {
    private final List<McpSyncClient> mcpSyncClients;

    @Override
    public List<McpServerAdminResponse> listMcpServers() {
        return mcpSyncClients.stream().map(mcpSyncClient -> {
            McpServerAdminResponse mcpServerAdminResponse = new McpServerAdminResponse();
            mcpServerAdminResponse.setName(mcpSyncClient.getServerInfo().name());
            List<McpToolAdminResponse> tools = mcpSyncClient.listTools().tools().stream().map(tool -> {
                McpToolAdminResponse mcpToolAdminResponse = new McpToolAdminResponse(tool.name(), tool.description());
                return mcpToolAdminResponse;
            }).collect(Collectors.toList());
            mcpServerAdminResponse.setTools(tools);

            return mcpServerAdminResponse;
        }).collect(Collectors.toList());
    }
}
