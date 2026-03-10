package org.mizoguchi.misaki.service.admin;

import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.pojo.dto.admin.AddAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.vo.admin.AssistantAdminResponse;

public interface AssistantAdminService {
    void addAssistant(AddAssistantAdminRequest addAssistantAdminRequest);
    PageResult<AssistantAdminResponse> searchAssistants(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchAssistantAdminRequest searchAssistantAdminRequest);
    void updateAssistant(Long assistantId, UpdateAssistantAdminRequest updateAssistantAdminRequest);
    void deleteAssistant(Long assistantId);
}
