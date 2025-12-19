package org.mizoguchi.misaki.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.ModelNotExistsException;
import org.mizoguchi.misaki.mapper.ModelMapper;
import org.mizoguchi.misaki.pojo.dto.admin.AddModelAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchModelAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateModelAdminRequest;
import org.mizoguchi.misaki.pojo.entity.Model;
import org.mizoguchi.misaki.pojo.vo.admin.ModelAdminResponse;
import org.mizoguchi.misaki.service.admin.ModelAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<ModelAdminResponse> searchModels(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchModelAdminRequest searchModelAdminRequest) {
        Page<Model> page = new Page<>(pageIndex, pageSize);

        List<Model> models = modelMapper.selectList(page, new QueryWrapper<Model>()
                .orderBy(sortField != null, sortOrder.equalsIgnoreCase("asc"), sortField)
                .lambda()
                .like(searchModelAdminRequest.getId() != null, Model::getId, searchModelAdminRequest.getId())
                .like(searchModelAdminRequest.getName() != null, Model::getName, searchModelAdminRequest.getName())
                .eq(searchModelAdminRequest.getGrade() != null, Model::getGrade, searchModelAdminRequest.getGrade())
                .eq(searchModelAdminRequest.getPrice() != null, Model::getPrice, searchModelAdminRequest.getPrice())
                .like(searchModelAdminRequest.getPath() != null, Model::getPath, searchModelAdminRequest.getPath())
                .like(searchModelAdminRequest.getAvatarPath() != null, Model::getAvatarPath, searchModelAdminRequest.getAvatarPath())
                .eq(searchModelAdminRequest.getCreateTime() != null, Model::getCreateTime, searchModelAdminRequest.getCreateTime())
        );

        return models.stream()
                .map(model -> {
                    ModelAdminResponse modelAdminResponse = new ModelAdminResponse();
                    BeanUtils.copyProperties(model, modelAdminResponse);

                    return modelAdminResponse;
                }).collect(Collectors.toList());
    }

    @Override
    public void updateModel(Long modelId, UpdateModelAdminRequest updateModelAdminRequest) {
        Model model = new Model();
        BeanUtils.copyProperties(updateModelAdminRequest, model);
        int affectedRows = modelMapper.update(model, new LambdaQueryWrapper<Model>().eq(Model::getId, modelId));

        if (affectedRows == 0) {
            throw new ModelNotExistsException(FailMessageConstant.MODEL_NOT_EXISTS);
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
