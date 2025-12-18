package org.mizoguchi.misaki.service.admin;

import jakarta.validation.constraints.Positive;
import org.mizoguchi.misaki.pojo.dto.admin.AddModelAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchModelAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateModelAdminRequest;
import org.mizoguchi.misaki.pojo.vo.admin.ModelAdminResponse;

import java.util.List;

public interface ModelAdminService {
    void addModel(AddModelAdminRequest addModelAdminRequest);
    List<ModelAdminResponse> searchModels(@Positive Integer pageIndex, @Positive Integer pageSize, String sortField, String sortOrder, SearchModelAdminRequest searchModelAdminRequest);
    void updateModel(Long modelId, UpdateModelAdminRequest updateModelAdminRequest);
    void deleteModel(Long modelId);
}
