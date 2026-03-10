package org.mizoguchi.misaki.service.front.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.CrystalNotEnoughException;
import org.mizoguchi.misaki.common.exception.ModelNotExistsException;
import org.mizoguchi.misaki.common.exception.PuzzleNotEnoughException;
import org.mizoguchi.misaki.common.exception.StardustNotEnoughException;
import org.mizoguchi.misaki.common.result.PageResult;
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
import java.util.concurrent.ThreadLocalRandom;
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
                .setIncrBy(User::getVersion, 1));
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
                .setIncrBy(User::getVersion, 1));
    }

    @Override
    @Transactional
    public List<WishFrontResponse> gacha(Long userId, Integer times) {
        User user = userMapper.selectById(userId);
        if (user.getPuzzle() < times) {
            throw new PuzzleNotEnoughException(FailMessageConstant.PUZZLE_NOT_ENOUGH);
        }

        List<WishFrontResponse> result = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < times; i++) {
            int r = random.nextInt(baseProbability);

            if (r <= tokenProbability) {
                result.add(drawToken(userId));
            } else if (r <= fourStarProbability) {
                result.add(drawModel(userId, 4, fourStarCompensation));
            } else {
                result.add(drawModel(userId, 5, fiveStarCompensation));
            }
        }

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .setDecrBy(User::getPuzzle, times)
                .setIncrBy(User::getVersion, 1));

        return result;
    }

    @Override
    public PageResult<WishFrontResponse> listWishes(Long userId, Integer pageIndex, Integer pageSize) {
        IPage<Wish> wishesPage = wishMapper.selectPage(new Page<>(pageIndex, pageSize), new LambdaQueryWrapper<Wish>()
                .eq(Wish::getUserId, userId)
                .orderBy(true, false, Wish::getCreateTime));

        PageResult<WishFrontResponse> pageResult = new PageResult<>();
        pageResult.setList(wishesPage.getRecords().stream().map(wish -> {
            WishFrontResponse wishFrontResponse = new WishFrontResponse();
            BeanUtils.copyProperties(wish, wishFrontResponse);
            return wishFrontResponse;
        }).collect(Collectors.toList()));

        pageResult.setTotal(wishesPage.getTotal());
        pageResult.setPageIndex(wishesPage.getCurrent());
        pageResult.setPageSize(wishesPage.getSize());

        return pageResult;
    }

    private WishFrontResponse drawToken(Long userId) {
        int token = ThreadLocalRandom.current().nextInt(tokenLimit);

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .setIncrBy(User::getToken, token)
                .setIncrBy(User::getVersion, 1));

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
    }

    private WishFrontResponse drawModel(Long userId, int grade, int compensation) {
        List<Model> models = modelMapper.selectList(
                new LambdaQueryWrapper<Model>().eq(Model::getGrade, grade));
        if (models.isEmpty()) {
            throw new ModelNotExistsException(FailMessageConstant.MODEL_NOT_EXISTS);
        }

        Model model = models.get(ThreadLocalRandom.current().nextInt(models.size()));

        ModelUser existing = modelUserMapper.selectOne(
                new LambdaQueryWrapper<ModelUser>()
                        .eq(ModelUser::getUserId, userId)
                        .eq(ModelUser::getModelId, model.getId()));

        Wish wish;
        if (existing != null) {
            userMapper.update(new LambdaUpdateWrapper<User>()
                    .eq(User::getId, userId)
                    .setIncrBy(User::getStardust, compensation)
                    .setIncrBy(User::getVersion, 1));

            wish = Wish.builder()
                    .userId(userId)
                    .hitFlag(true)
                    .duplicateFlag(true)
                    .modelId(model.getId())
                    .amount(compensation)
                    .build();
        } else {
            modelUserMapper.insert(
                    ModelUser.builder()
                            .userId(userId)
                            .modelId(model.getId())
                            .build());

            wish = Wish.builder()
                    .userId(userId)
                    .hitFlag(true)
                    .duplicateFlag(false)
                    .modelId(model.getId())
                    .amount(1)
                    .build();
        }

        wishMapper.insert(wish);

        WishFrontResponse wishFrontResponse = new WishFrontResponse();
        BeanUtils.copyProperties(wish, wishFrontResponse);

        return wishFrontResponse;
    }

}
