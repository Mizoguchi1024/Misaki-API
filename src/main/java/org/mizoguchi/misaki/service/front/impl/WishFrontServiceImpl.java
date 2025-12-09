package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.CrystalNotEnoughException;
import org.mizoguchi.misaki.common.exception.StardustNotEnoughException;
import org.mizoguchi.misaki.mapper.ModelMapper;
import org.mizoguchi.misaki.mapper.ModelUserMapper;
import org.mizoguchi.misaki.mapper.UserMapper;
import org.mizoguchi.misaki.mapper.WishMapper;
import org.mizoguchi.misaki.pojo.entity.Model;
import org.mizoguchi.misaki.pojo.entity.ModelUser;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.entity.Wish;
import org.mizoguchi.misaki.pojo.vo.front.WishFrontResponse;
import org.mizoguchi.misaki.service.front.WishFrontService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class WishFrontServiceImpl implements WishFrontService {
    private final WishMapper wishMapper;
    private final UserMapper userMapper;
    private final ModelMapper modelMapper;
    private final ModelUserMapper modelUserMapper;

    @Override
    public void buyPuzzleWithCrystal(Long userId, Integer amount) {
        User user = userMapper.selectById(userId);

        if (user.getCrystal() <= amount * 160) {
            throw new CrystalNotEnoughException(FailMessageConstant.CRYSTAL_NOT_ENOUGH);
        }

        user.setCrystal(user.getCrystal() - amount * 160);
        user.setPuzzle(user.getPuzzle() + amount);

        userMapper.updateById(user);
    }

    @Override
    public void buyPuzzleWithStardust(Long userId, Integer amount) {
        User user = userMapper.selectById(userId);

        if (user.getStardust() <= amount * 20) {
            throw new StardustNotEnoughException(FailMessageConstant.STARDUST_NOT_ENOUGH);
        }

        user.setStardust(user.getStardust() - amount * 20);
        user.setPuzzle(user.getPuzzle() + amount);

        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public WishFrontResponse gacha(Long userId, Integer amount) {
        Random random = new Random(System.currentTimeMillis());

        int randomResult = random.nextInt(1000);

        if (randomResult <= 900) { // 抽到token
            int token = random.nextInt(10000);

            User user = userMapper.selectById(userId);

            userMapper.update(new LambdaUpdateWrapper<User>()
                    .eq(User::getId, userId)
                    .set(User::getToken, user.getToken() + token));

            Wish wish = Wish.builder()
                    .userId(userId)
                    .hitFlag(false)
                    .duplicateFlag(false)
                    .amount(token)
                    .build();

            wishMapper.insert(wish);

            WishFrontResponse wishFrontResponse = new WishFrontResponse();
            BeanUtils.copyProperties(wish, wishFrontResponse);

            return wishFrontResponse;
        } else if (randomResult <= 994) { // 抽到4星模型
            List<Model> models = modelMapper.selectList(new LambdaQueryWrapper<Model>()
                    .eq(Model::getGrade, 4));

            int index = random.nextInt(0,models.size() - 1);

            ModelUser existingModel = modelUserMapper.selectOne(new LambdaQueryWrapper<ModelUser>()
                    .eq(ModelUser::getUserId, userId)
                    .eq(ModelUser::getModelId, models.get(index).getId()));

            if (existingModel != null) { // 抽到已有模型
                User user = userMapper.selectById(userId);

                userMapper.update(new LambdaUpdateWrapper<User>()
                        .eq(User::getId, userId)
                        .set(User::getStardust, user.getStardust() + 100));

                Wish wish = Wish.builder()
                        .userId(userId)
                        .hitFlag(true)
                        .duplicateFlag(true)
                        .modelId(models.get(index).getId())
                        .amount(100) // 补偿100星尘
                        .build();

                wishMapper.insert(wish);

                WishFrontResponse wishFrontResponse = new WishFrontResponse();
                BeanUtils.copyProperties(wish, wishFrontResponse);

                return wishFrontResponse;
            }else{ // 抽到未拥有模型
                ModelUser modelUser = ModelUser.builder()
                        .userId(userId)
                        .modelId(models.get(index).getId())
                        .build();

                modelUserMapper.insert(modelUser);

                Wish wish = Wish.builder()
                        .userId(userId)
                        .hitFlag(true)
                        .duplicateFlag(false)
                        .modelId(models.get(index).getId())
                        .amount(1)
                        .build();

                wishMapper.insert(wish);

                WishFrontResponse wishFrontResponse = new WishFrontResponse();
                BeanUtils.copyProperties(wish, wishFrontResponse);

                return wishFrontResponse;
            }
        } else { // 抽到5星模型
            List<Model> models = modelMapper.selectList(new LambdaQueryWrapper<Model>()
                    .eq(Model::getGrade, 5));

            int index = random.nextInt(0,models.size() - 1);

            ModelUser existingModel = modelUserMapper.selectOne(new LambdaQueryWrapper<ModelUser>()
                    .eq(ModelUser::getUserId, userId)
                    .eq(ModelUser::getModelId, models.get(index).getId()));

            if (existingModel != null) { // 抽到已有模型
                User user = userMapper.selectById(userId);

                userMapper.update(new LambdaUpdateWrapper<User>()
                        .eq(User::getId, userId)
                        .set(User::getStardust, user.getStardust() + 200));

                Wish wish = Wish.builder()
                        .userId(userId)
                        .hitFlag(true)
                        .duplicateFlag(true)
                        .modelId(models.get(index).getId())
                        .amount(200) // 补偿200星尘
                        .build();

                wishMapper.insert(wish);

                WishFrontResponse wishFrontResponse = new WishFrontResponse();
                BeanUtils.copyProperties(wish, wishFrontResponse);

                return wishFrontResponse;
            }else{ // 抽到未拥有模型
                ModelUser modelUser = ModelUser.builder()
                        .userId(userId)
                        .modelId(models.get(index).getId())
                        .build();

                modelUserMapper.insert(modelUser);

                Wish wish = Wish.builder()
                        .userId(userId)
                        .hitFlag(true)
                        .duplicateFlag(false)
                        .modelId(models.get(index).getId())
                        .amount(1)
                        .build();

                wishMapper.insert(wish);

                WishFrontResponse wishFrontResponse = new WishFrontResponse();
                BeanUtils.copyProperties(wish, wishFrontResponse);

                return wishFrontResponse;
            }
        }
    }

    @Override
    public List<WishFrontResponse> listWishes(Long userId) {
        // TODO
        return List.of();
    }
}
