package org.mizoguchi.misaki.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.SqlConstant;
import org.mizoguchi.misaki.common.exception.WishNotExistsException;
import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.mapper.WishMapper;
import org.mizoguchi.misaki.pojo.dto.admin.SearchWishAdminRequest;
import org.mizoguchi.misaki.pojo.entity.Wish;
import org.mizoguchi.misaki.pojo.vo.admin.WishAdminResponse;
import org.mizoguchi.misaki.service.admin.WishAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishAdminServiceImpl implements WishAdminService {
    private final WishMapper wishMapper;

    @Override
    public PageResult<WishAdminResponse> searchWishes(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchWishAdminRequest searchWishAdminRequest) {
        IPage<Wish> wishes = wishMapper.selectPage(new Page<>(pageIndex, pageSize), new QueryWrapper<Wish>()
                .orderBy(sortField != null, sortOrder.equalsIgnoreCase(SqlConstant.ASC), sortField)
                .lambda()
                .like(searchWishAdminRequest.getId() != null, Wish::getId, searchWishAdminRequest.getId())
                .like(searchWishAdminRequest.getUserId() != null, Wish::getUserId, searchWishAdminRequest.getUserId())
                .eq(searchWishAdminRequest.getHitFlag() != null, Wish::getHitFlag, searchWishAdminRequest.getHitFlag())
                .eq(searchWishAdminRequest.getDuplicateFlag() != null, Wish::getDuplicateFlag, searchWishAdminRequest.getDuplicateFlag())
                .like(searchWishAdminRequest.getModelId() != null, Wish::getModelId, searchWishAdminRequest.getModelId())
                .eq(searchWishAdminRequest.getAmount() != null, Wish::getAmount, searchWishAdminRequest.getAmount())
                .like(searchWishAdminRequest.getCreateTime() != null, Wish::getCreateTime, searchWishAdminRequest.getCreateTime())
        );

        PageResult<WishAdminResponse> pageResult = new PageResult<>();
        pageResult.setList(wishes.getRecords().stream()
                .map(wish -> {
                    WishAdminResponse wishAdminResponse = new WishAdminResponse();
                    BeanUtils.copyProperties(wish, wishAdminResponse);

                    return wishAdminResponse;
                }).collect(Collectors.toList()));

        pageResult.setTotal(Math.toIntExact(wishes.getTotal()));
        pageResult.setPageIndex(Math.toIntExact(wishes.getCurrent()));
        pageResult.setPageSize(Math.toIntExact(wishes.getSize()));

        return pageResult;
    }

    @Override
    public void deleteWish(Long wishId) {
        int affectedRows = wishMapper.deleteById(wishId);

        if (affectedRows == 0) {
            throw new WishNotExistsException(FailMessageConstant.WISH_NOT_EXISTS);
        }
    }

    @Override
    public void deleteWishes(LocalDate date) {
        int affectedRows = wishMapper.delete(new LambdaQueryWrapper<Wish>()
                .le(Wish::getCreateTime, date)
        );

        if (affectedRows == 0) {
            throw new WishNotExistsException(FailMessageConstant.WISH_NOT_EXISTS);
        }
    }
}
