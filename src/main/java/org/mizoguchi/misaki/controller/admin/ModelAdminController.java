package org.mizoguchi.misaki.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.InvalidSortParamsException;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.admin.AddModelAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchModelAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateModelAdminRequest;
import org.mizoguchi.misaki.pojo.entity.Model;
import org.mizoguchi.misaki.pojo.vo.admin.ModelAdminResponse;
import org.mizoguchi.misaki.service.admin.ModelAdminService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/models")
@RequiredArgsConstructor
@Tag(name = "管理端-模型相关接口")
public class ModelAdminController {
    private final ModelAdminService modelAdminService;

    @Operation(summary = "创建模型")
    @PostMapping()
    public Result<Void> createModel(@RequestBody @Validated AddModelAdminRequest addModelAdminRequest){
        modelAdminService.addModel(addModelAdminRequest);
        return Result.success();
    }

    @Operation(summary = "分页条件搜索模型")
    @PostMapping("/search")
    public Result<List<ModelAdminResponse>> searchModels(@RequestParam @Positive Integer pageIndex,
                                                         @RequestParam @Positive Integer pageSize,
                                                         @RequestParam(required = false) String sortField,
                                                         @RequestParam(defaultValue = "asc") String sortOrder,
                                                         @RequestBody @Validated SearchModelAdminRequest searchModelAdminRequest){
        if (sortField != null && !sortField.isBlank()){
            try {
                Model.class.getDeclaredField(sortField);
            } catch (NoSuchFieldException e) {
                throw new InvalidSortParamsException(FailMessageConstant.INVALID_SORT_PARAMS);
            }
        }
        return Result.success(modelAdminService.searchModels(pageIndex, pageSize, sortField, sortOrder, searchModelAdminRequest));
    }

    @Operation(summary = "修改模型")
    @PutMapping("/{id}")
    public Result<Void> updateModel(@PathVariable Long id, @RequestBody @Validated UpdateModelAdminRequest updateModelAdminRequest){
        modelAdminService.updateModel(id, updateModelAdminRequest);
        return Result.success();
    }

    @Operation(summary = "删除模型")
    @DeleteMapping("/{id}")
    public Result<Void> deleteModel(@PathVariable Long id){
        modelAdminService.deleteModel(id);
        return Result.success();
    }
}
