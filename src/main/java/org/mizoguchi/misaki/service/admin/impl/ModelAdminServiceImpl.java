package org.mizoguchi.misaki.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.SqlConstant;
import org.mizoguchi.misaki.common.exception.ModelNotExistsException;
import org.mizoguchi.misaki.common.exception.OptimisticLockFailedException;
import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.mapper.ModelMapper;
import org.mizoguchi.misaki.pojo.dto.admin.AddModelAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchModelAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateModelAdminRequest;
import org.mizoguchi.misaki.pojo.entity.Model;
import org.mizoguchi.misaki.pojo.vo.admin.ModelAdminResponse;
import org.mizoguchi.misaki.service.admin.ModelAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModelAdminServiceImpl implements ModelAdminService {
    private final ModelMapper modelMapper;

    @Override
    public void addModel(AddModelAdminRequest addModelAdminRequest) {
        Model model = new Model();
        BeanUtils.copyProperties(addModelAdminRequest, model);
        modelMapper.insert(model);
    }

    @Override
    public PageResult<ModelAdminResponse> searchModels(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchModelAdminRequest searchModelAdminRequest) {
        IPage<Model> models = modelMapper.selectPage(new Page<>(pageIndex, pageSize), new QueryWrapper<Model>()
                .orderBy(sortField != null, sortOrder.equalsIgnoreCase(SqlConstant.ASC), sortField)
                .lambda()
                .like(searchModelAdminRequest.getId() != null, Model::getId, searchModelAdminRequest.getId())
                .like(searchModelAdminRequest.getName() != null, Model::getName, searchModelAdminRequest.getName())
                .eq(searchModelAdminRequest.getGrade() != null, Model::getGrade, searchModelAdminRequest.getGrade())
                .eq(searchModelAdminRequest.getPrice() != null, Model::getPrice, searchModelAdminRequest.getPrice())
                .like(searchModelAdminRequest.getCreateTime() != null, Model::getCreateTime, searchModelAdminRequest.getCreateTime())
        );

        PageResult<ModelAdminResponse> pageResult = new PageResult<>();
        pageResult.setList(models.getRecords().stream()
                .map(model -> {
                    ModelAdminResponse modelAdminResponse = new ModelAdminResponse();
                    BeanUtils.copyProperties(model, modelAdminResponse);

                    return modelAdminResponse;
                }).collect(Collectors.toList()));

        pageResult.setTotal(Math.toIntExact(models.getTotal()));
        pageResult.setPageIndex(Math.toIntExact(models.getCurrent()));
        pageResult.setPageSize(Math.toIntExact(models.getSize()));

        return pageResult;
    }

    @Override
    public void updateModel(Long modelId, UpdateModelAdminRequest updateModelAdminRequest) {
        boolean existsFlag = modelMapper.exists(new LambdaQueryWrapper<Model>()
                .eq(Model::getId, modelId)
        );

        if (!existsFlag) {
            throw new ModelNotExistsException(FailMessageConstant.MODEL_NOT_EXISTS);
        }

        Model model = new Model();
        BeanUtils.copyProperties(updateModelAdminRequest, model);
        model.setId(modelId);
        int affectedRows = modelMapper.updateById(model);

        if (affectedRows == 0) {
            throw new OptimisticLockFailedException(FailMessageConstant.OPTIMISTIC_LOCK_FAILED);
        }
    }

    @Override
    public void deleteModel(Long modelId) {
        int affectedRows = modelMapper.deleteById(modelId);

        if (affectedRows == 0) {
            throw new ModelNotExistsException(FailMessageConstant.MODEL_NOT_EXISTS);
        }
    }
}
