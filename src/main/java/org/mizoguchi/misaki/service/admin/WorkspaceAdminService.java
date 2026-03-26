package org.mizoguchi.misaki.service.admin;

import org.mizoguchi.misaki.pojo.vo.admin.WorkspaceAdminResponse;

public interface WorkspaceAdminService {
    WorkspaceAdminResponse getData(String range);
}
