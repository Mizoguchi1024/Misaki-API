package org.mizoguchi.misaki.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.InvalidSortParamsException;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.admin.SearchWishAdminRequest;
import org.mizoguchi.misaki.pojo.entity.Wish;
import org.mizoguchi.misaki.pojo.vo.admin.WishAdminResponse;
import org.mizoguchi.misaki.service.admin.WishAdminService;
import org.springframework.data.util.ParsingUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/wish")
@RequiredArgsConstructor
@Tag(name = "管理端-祈愿相关接口")
public class WishAdminController {
    private final WishAdminService wishAdminService;

    @Operation(summary = "分页条件搜索祈愿记录")
    @PostMapping("/search")
    public Result<List<WishAdminResponse>> searchWishes(@RequestParam @Positive Integer pageIndex,
                                                        @RequestParam @Positive Integer pageSize,
                                                        @RequestParam(required = false) String sortField,
                                                        @RequestParam(defaultValue = "asc") String sortOrder,
                                                        @RequestBody @Validated SearchWishAdminRequest searchWishAdminRequest){
        if (sortField != null && !sortField.isBlank()){
            try {
                Wish.class.getDeclaredField(sortField);
            } catch (NoSuchFieldException e) {
                throw new InvalidSortParamsException(FailMessageConstant.INVALID_SORT_PARAMS);
            }
            sortField = ParsingUtils.reconcatenateCamelCase(sortField, "_");
        }
        return Result.success(wishAdminService.searchWishes(pageIndex, pageSize, sortField, sortOrder, searchWishAdminRequest));
    }

    @Operation(summary = "删除祈愿记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteWish(@PathVariable Long id){
        wishAdminService.deleteWish(id);
        return Result.success();
    }

    @Operation(summary = "删除某日期及以前的祈愿记录")
    @DeleteMapping()
    public Result<Void> deleteWishes(@RequestParam LocalDate date){
        wishAdminService.deleteWishes(date);
        return Result.success();
    }
}
