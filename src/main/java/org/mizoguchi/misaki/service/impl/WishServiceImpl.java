package org.mizoguchi.misaki.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.CrystalNotEnoughException;
import org.mizoguchi.misaki.common.exception.ModelAlreadyHaveException;
import org.mizoguchi.misaki.common.exception.ModelNotExistsExption;
import org.mizoguchi.misaki.common.exception.StardustNotEnoughException;
import org.mizoguchi.misaki.mapper.ModelMapper;
import org.mizoguchi.misaki.mapper.ModelUserMapper;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.pojo.entity.Model;
import org.mizoguchi.misaki.pojo.entity.ModelUser;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.vo.front.ModelFrontResponse;
import org.mizoguchi.misaki.pojo.vo.front.WishFrontResponse;
import org.mizoguchi.misaki.service.WishService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishServiceImpl implements WishService {
    private final UserMapper userMapper;
    private final ModelMapper modelMapper;
    private final ModelUserMapper modelUserMapper;

    @Override
    public void buyPuzzle(Long userId, Integer amount) {
        User user = userMapper.selectById(userId);

        if (user.getCrystal() <= amount * 160) {
            throw new CrystalNotEnoughException(FailMessageConstant.CRYSTAL_NOT_ENOUGH);
        }

        user.setCrystal(user.getCrystal() - amount * 160);
        user.setPuzzle(user.getPuzzle() + amount);

        userMapper.updateById(user);
    }

    @Override
    public void buyModel(Long userId, Long modelId) {
        Model model = modelMapper.selectById(modelId);

        if (model == null){
            throw new ModelNotExistsExption(FailMessageConstant.MODEL_NOT_EXISTS);
        }

        ModelUser existingModelUser = modelUserMapper.selectOne(new LambdaQueryWrapper<ModelUser>()
                .eq(ModelUser::getUserId, userId)
                .eq(ModelUser::getModelId, modelId));

        if (existingModelUser != null) {
            throw new ModelAlreadyHaveException(FailMessageConstant.MODEL_ALREADY_HAVE);
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

    @Override
    public WishFrontResponse wish(Long userId, Integer amount) {
        WishFrontResponse wishFrontResponse = new WishFrontResponse();

        double r = Math.random(); // 0~1 之间

        if (r < 0.006) {                 // 0.6%
            wishFrontResponse.setPrize(5);
            return wishFrontResponse;
        } else if (r < 0.006 + 0.094) {  // 9.4%
            wishFrontResponse.setPrize(4);
            return wishFrontResponse;
        } else {                         // 90%
            wishFrontResponse.setPrize(3);
            return wishFrontResponse;
        }
    }

    @Override
    public List<ModelFrontResponse> listModelFrontResponse(Long userId) {
        return List.of();
    }

    @Override
    public List<WishFrontResponse> listWishFrontResponse(Long userId) {
        return List.of();
    }
}
