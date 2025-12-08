package org.mizoguchi.misaki.service.admin;

import org.mizoguchi.misaki.pojo.dto.admin.AddAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateAssistantAdminRequest;
import org.mizoguchi.misaki.pojo.vo.admin.AssistantAdminResponse;

import java.util.List;

public interface AssistantAdminService {
    void addAssistant(AddAssistantAdminRequest addAssistantAdminRequest);
    List<AssistantAdminResponse> listAssistants(Integer pageIndex, Integer pageSize);
    List<AssistantAdminResponse> searchAssistants(SearchAssistantAdminRequest searchAssistantAdminRequest);
    void updateAssistant(Long assistantId, UpdateAssistantAdminRequest updateAssistantAdminRequest);
    void deleteAssistant(Long assistantId);
}
