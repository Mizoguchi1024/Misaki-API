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
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private Integer puzzleStardust;

    @Value("${misaki.business.wish.compensation.four-star}")
    private Integer fourStarCompensation;

    @Value("${misaki.business.wish.compensation.five-star}")
    private Integer fiveStarCompensation;

    @Value("${misaki.business.wish.token.limit}")
    private Integer tokenLimit;

    @Value("${misaki.business.wish.probability.four-star}")
    private double fourStarBaseProbability;

    @Value("${misaki.business.wish.probability.five-star}")
    private double fiveStarBaseProbability;

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

        if (user.getStardust() <= amount * puzzleStardust) {
            throw new StardustNotEnoughException(FailMessageConstant.STARDUST_NOT_ENOUGH);
        }

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .setDecrBy(User::getStardust, amount * puzzleStardust)
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

        List<Wish> wishes = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int countFromLastFourHit = wishMapper.countFromLastHit(userId, 4);
        int countFromLastFiveHit = wishMapper.countFromLastHit(userId, 5);

        for (int i = 0; i < times; i++) {
            double fourStarActualProbability = calculateActualProbability(countFromLastFourHit, 6, fourStarBaseProbability);
            double fiveStarActualProbability = calculateActualProbability(countFromLastFiveHit, 72, fiveStarBaseProbability);

            boolean hitFourStar = random.nextDouble() < fourStarActualProbability;
            boolean hitFiveStar = random.nextDouble() < fiveStarActualProbability;

            if (hitFiveStar) {
                wishes.add(drawModel(userId, 5, fiveStarCompensation));
                countFromLastFiveHit = 0;
            } else if (hitFourStar) {
                wishes.add(drawModel(userId, 4, fourStarCompensation));
                countFromLastFourHit = 0;
                countFromLastFiveHit++;
            } else {
                wishes.add(drawToken(userId));
                countFromLastFourHit++;
                countFromLastFiveHit++;
            }
        }

        wishMapper.insert(wishes);

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .setDecrBy(User::getPuzzle, times)
                .setIncrBy(User::getVersion, 1)
        );

        return buildWishFrontResponses(wishes);
    }

    @Override
    public PageResult<WishFrontResponse> listWishes(Long userId, Integer pageIndex, Integer pageSize) {
        IPage<Wish> wishesPage = wishMapper.selectPage(new Page<>(pageIndex, pageSize), new LambdaQueryWrapper<Wish>()
                .eq(Wish::getUserId, userId)
                .orderBy(true, false, Wish::getCreateTime));

        PageResult<WishFrontResponse> pageResult = new PageResult<>();
        pageResult.setList(buildWishFrontResponses(wishesPage.getRecords()));

        pageResult.setTotal(Math.toIntExact(wishesPage.getTotal()));
        pageResult.setPageIndex(Math.toIntExact(wishesPage.getCurrent()));
        pageResult.setPageSize(Math.toIntExact(wishesPage.getSize()));

        return pageResult;
    }

    private double calculateActualProbability(int missCount, int softPityThreshold, double baseProbability) {
        if (missCount <= softPityThreshold) {
            return baseProbability;
        }

        return Math.min(1.0, baseProbability + 10.0 * baseProbability * (missCount - softPityThreshold));
    }

    private Wish drawToken(Long userId) {
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

        return wish;
    }

    private Wish drawModel(Long userId, int grade, int compensation) {
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

        return wish;
    }

    private List<WishFrontResponse> buildWishFrontResponses(List<Wish> wishes) {
        List<Long> modelIds = wishes.stream()
                .map(Wish::getModelId)
                .filter(modelId -> modelId != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Model> modelMap = modelIds.isEmpty()
                ? Collections.emptyMap()
                : modelMapper.selectByIds(modelIds).stream()
                .collect(Collectors.toMap(Model::getId, model -> model));

        return wishes.stream().map(wish -> {
            WishFrontResponse wishFrontResponse = new WishFrontResponse();
            BeanUtils.copyProperties(wish, wishFrontResponse);

            Model model = modelMap.get(wish.getModelId());
            if (model != null) {
                wishFrontResponse.setModelName(model.getName());
                wishFrontResponse.setModelGrade(model.getGrade());
                wishFrontResponse.setModelAvatarPath(model.getAvatarPath());
            }

            return wishFrontResponse;
        }).collect(Collectors.toList());
    }

}
