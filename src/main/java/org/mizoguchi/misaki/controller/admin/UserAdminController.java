package org.mizoguchi.misaki.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.InvalidSortParamsException;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.admin.AddUserAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchUserAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.UpdateUserAdminRequest;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.vo.admin.UserAdminResponse;
import org.mizoguchi.misaki.service.admin.UserAdminService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "管理端-用户相关接口")
public class UserAdminController {
    private final UserAdminService userAdminService;

    @Operation(summary = "创建用户")
    @PostMapping()
    public Result<Void> createUser(@RequestBody AddUserAdminRequest addUserAdminRequest){
        userAdminService.addUser(addUserAdminRequest);
        return Result.success();
    }

    @Operation(summary = "分页条件搜索用户")
    @PostMapping("/search")
    public Result<List<UserAdminResponse>> searchUsers(@RequestParam @Positive Integer pageIndex,
                                                       @RequestParam @Positive Integer pageSize,
                                                       @RequestParam(required = false) String sortField,
                                                       @RequestParam(required = false) String sortOrder,
                                                       SearchUserAdminRequest searchUserAdminRequest){
        if (sortField != null && !sortField.isBlank()){
            try {
                User.class.getDeclaredField(sortField);
            } catch (NoSuchFieldException e) {
                throw new InvalidSortParamsException(FailMessageConstant.INVALID_SORT_PARAMS);
            }
        }
        return Result.success(userAdminService.searchUsers(pageIndex, pageSize, sortField, sortOrder, searchUserAdminRequest));
    }

    @Operation(summary = "修改用户")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable String id, @RequestBody UpdateUserAdminRequest updateUserAdminRequest){
        userAdminService.updateUser(Long.valueOf(id), updateUserAdminRequest);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable String id){
        userAdminService.deleteUser(Long.valueOf(id));
        return Result.success();
    }
}
