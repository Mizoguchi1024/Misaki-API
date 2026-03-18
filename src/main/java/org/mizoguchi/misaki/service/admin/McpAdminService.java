package org.mizoguchi.misaki.service.admin;

import java.util.List;

import org.mizoguchi.misaki.pojo.vo.admin.McpServerAdminResponse;

public interface McpAdminService {
    List<McpServerAdminResponse> listMcpServers();
}
