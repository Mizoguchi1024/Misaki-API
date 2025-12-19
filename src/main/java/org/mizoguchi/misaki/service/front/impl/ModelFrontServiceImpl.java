package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.ModelAlreadyOwnedException;
import org.mizoguchi.misaki.common.exception.ModelNotExistsException;
import org.mizoguchi.misaki.common.exception.StardustNotEnoughException;
import org.mizoguchi.misaki.mapper.ModelMapper;
import org.mizoguchi.misaki.mapper.ModelUserMapper;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.pojo.entity.Model;
import org.mizoguchi.misaki.pojo.entity.ModelUser;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.vo.front.ModelFrontResponse;
import org.mizoguchi.misaki.service.front.ModelFrontService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModelFrontServiceImpl implements ModelFrontService {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private final ModelUserMapper modelUserMapper;

    @Override
    public List<ModelFrontResponse> listModels(Long userId) {
        return modelMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .map(model -> {
                    ModelFrontResponse modelFrontResponse = new ModelFrontResponse();
                    BeanUtils.copyProperties(model, modelFrontResponse);

                    ModelUser existingModelUser = modelUserMapper.selectOne(new LambdaQueryWrapper<ModelUser>()
                            .eq(ModelUser::getUserId, userId)
                            .eq(ModelUser::getModelId, model.getId()));

                    modelFrontResponse.setOwnedFlag(existingModelUser != null);

                    return modelFrontResponse;
                }).collect(Collectors.toList());
    }

    @Override
    public void buyModel(Long userId, Long modelId) {
        Model model = modelMapper.selectById(modelId);

        if (model == null){
            throw new ModelNotExistsException(FailMessageConstant.MODEL_NOT_EXISTS);
        }

        ModelUser existingModelUser = modelUserMapper.selectOne(new LambdaQueryWrapper<ModelUser>()
                .eq(ModelUser::getUserId, userId)
                .eq(ModelUser::getModelId, modelId));

        if (existingModelUser != null) {
            throw new ModelAlreadyOwnedException(FailMessageConstant.MODEL_ALREADY_OWNED);
        }

        User user = userMapper.selectById(userId);
        if (user.getStardust() <= model.getPrice()) {
            throw new StardustNotEnoughException(FailMessageConstant.STARDUST_NOT_ENOUGH);
        }

        user.setStardust(user.getStardust() - model.getPrice());
        userMapper.updateById(user);

        ModelUser modelUser = ModelUser.builder()
                .userId(userId)
                .modelId(modelId)
                .build();

        modelUserMapper.insert(modelUser);
    }
}
