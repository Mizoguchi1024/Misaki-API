package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.CrystalNotEnoughException;
import org.mizoguchi.misaki.common.exception.ModelNotExistsException;
import org.mizoguchi.misaki.common.exception.PuzzleNotEnoughException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WishFrontServiceImpl implements WishFrontService {
    private final WishMapper wishMapper;
    private final UserMapper userMapper;
    private final ModelMapper modelMapper;
    private final ModelUserMapper modelUserMapper;

    @Value("${misaki.business.wish.exchange-rate.puzzle-crystal}")
    private Integer puzzleCrystal;

    @Value("${misaki.business.wish.exchange-rate.puzzle-stardust}")
    private Integer stardust;

    @Value("${misaki.business.wish.compensation.four-star}")
    private Integer fourStarCompensation;

    @Value("${misaki.business.wish.compensation.five-star}")
    private Integer fiveStarCompensation;

    @Value("${misaki.business.wish.token.limit}")
    private Integer tokenLimit;

    @Value("${misaki.business.wish.probability.base}")
    private Integer baseProbability;

    @Value("${misaki.business.wish.probability.token}")
    private Integer tokenProbability;

    @Value("${misaki.business.wish.probability.four-star}")
    private Integer fourStarProbability;


    @Override
    public void buyPuzzleWithCrystal(Long userId, Integer amount) {
        User user = userMapper.selectById(userId);

        if (user.getCrystal() <= amount * puzzleCrystal) {
            throw new CrystalNotEnoughException(FailMessageConstant.CRYSTAL_NOT_ENOUGH);
        }

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .setDecrBy(User::getCrystal, amount * puzzleCrystal)
                .setIncrBy(User::getPuzzle, amount)
                .setIncrBy(User::getVersion, 1)
        );
    }

    @Override
    public void buyPuzzleWithStardust(Long userId, Integer amount) {
        User user = userMapper.selectById(userId);

        if (user.getStardust() <= amount * stardust) {
            throw new StardustNotEnoughException(FailMessageConstant.STARDUST_NOT_ENOUGH);
        }

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .setDecrBy(User::getStardust, amount * stardust)
                .setIncrBy(User::getPuzzle, amount)
                .setIncrBy(User::getVersion, 1)
        );
    }

    @Override
    @Transactional
    public List<WishFrontResponse> gacha(Long userId, Integer times) {
        User user = userMapper.selectById(userId);

        if (user.getPuzzle() < times) {
            throw new PuzzleNotEnoughException(FailMessageConstant.PUZZLE_NOT_ENOUGH);
        }

        List<WishFrontResponse> wishFrontResponses = new ArrayList<>();

        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < times; i++) {
            int randomResult = random.nextInt(baseProbability);

            if (randomResult <= tokenProbability) { // 抽到 token
                int token = random.nextInt(tokenLimit);

                userMapper.update(new LambdaUpdateWrapper<User>()
                        .eq(User::getId, userId)
                        .setIncrBy(User::getToken, token)
                        .setIncrBy(User::getVersion, 1)
                );

                Wish wish = Wish.builder()
                        .userId(userId)
                        .hitFlag(false)
                        .duplicateFlag(false)
                        .amount(token)
                        .build();

                wishMapper.insert(wish);

                WishFrontResponse wishFrontResponse = new WishFrontResponse();
                BeanUtils.copyProperties(wish, wishFrontResponse);

                wishFrontResponses.add(wishFrontResponse);
            } else if (randomResult <= fourStarProbability) { // 抽到4星模型
                List<Model> models = modelMapper.selectList(new LambdaQueryWrapper<Model>()
                        .eq(Model::getGrade, 4));

                if (models.isEmpty()) {
                    throw new ModelNotExistsException(FailMessageConstant.MODEL_NOT_EXISTS);
                }

                int index = random.nextInt(0, models.size());

                ModelUser existingModel = modelUserMapper.selectOne(new LambdaQueryWrapper<ModelUser>()
                        .eq(ModelUser::getUserId, userId)
                        .eq(ModelUser::getModelId, models.get(index).getId()));

                if (existingModel != null) { // 抽到已有模型
                    userMapper.update(new LambdaUpdateWrapper<User>()
                            .eq(User::getId, userId)
                            .setIncrBy(User::getStardust, fourStarCompensation)
                            .setIncrBy(User::getVersion, 1)
                );

                    Wish wish = Wish.builder()
                            .userId(userId)
                            .hitFlag(true)
                            .duplicateFlag(true)
                            .modelId(models.get(index).getId())
                            .amount(fourStarCompensation) // 补偿100星尘
                            .build();

                    wishMapper.insert(wish);

                    WishFrontResponse wishFrontResponse = new WishFrontResponse();
                    BeanUtils.copyProperties(wish, wishFrontResponse);

                    wishFrontResponses.add(wishFrontResponse);
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

                    wishFrontResponses.add(wishFrontResponse);
                }
            } else { // 抽到5星模型
                List<Model> models = modelMapper.selectList(new LambdaQueryWrapper<Model>()
                        .eq(Model::getGrade, 5));

                if (models.isEmpty()) {
                    throw new ModelNotExistsException(FailMessageConstant.MODEL_NOT_EXISTS);
                }

                int index = random.nextInt(0, models.size());

                ModelUser existingModel = modelUserMapper.selectOne(new LambdaQueryWrapper<ModelUser>()
                        .eq(ModelUser::getUserId, userId)
                        .eq(ModelUser::getModelId, models.get(index).getId()));

                if (existingModel != null) { // 抽到已有模型
                    userMapper.update(new LambdaUpdateWrapper<User>()
                            .eq(User::getId, userId)
                            .setIncrBy(User::getStardust, fiveStarCompensation)
                            .setIncrBy(User::getVersion, 1)
                );

                    Wish wish = Wish.builder()
                            .userId(userId)
                            .hitFlag(true)
                            .duplicateFlag(true)
                            .modelId(models.get(index).getId())
                            .amount(fiveStarCompensation) // 补偿200星尘
                            .build();

                    wishMapper.insert(wish);

                    WishFrontResponse wishFrontResponse = new WishFrontResponse();
                    BeanUtils.copyProperties(wish, wishFrontResponse);

                    wishFrontResponses.add(wishFrontResponse);
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

                    wishFrontResponses.add(wishFrontResponse);
                }
            }
        }
        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .setDecrBy(User::getPuzzle, times)
                .setIncrBy(User::getVersion, 1)
        );

        return wishFrontResponses;
    }

    @Override
    public List<WishFrontResponse> listWishes(Long userId, Integer pageIndex, Integer pageSize) {
        List<Wish> wishes = wishMapper.selectList(new Page<>(pageIndex, pageSize), new LambdaQueryWrapper<Wish>()
                .eq(Wish::getUserId, userId)
                .orderBy(true, false, Wish::getCreateTime));

        return wishes.stream()
                .map(wish -> {
                    WishFrontResponse wishFrontResponse = new WishFrontResponse();
                    BeanUtils.copyProperties(wish, wishFrontResponse);
                    return wishFrontResponse;
                }).collect(Collectors.toList());
    }
}
