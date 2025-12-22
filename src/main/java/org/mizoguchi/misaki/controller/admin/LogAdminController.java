package org.mizoguchi.misaki.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.InvalidSortParamsException;
import org.mizoguchi.misaki.common.result.Result;
import org.mizoguchi.misaki.pojo.dto.admin.SearchEmailLogAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchExceptionLogAdminRequest;
import org.mizoguchi.misaki.pojo.entity.User;
import org.mizoguchi.misaki.pojo.vo.admin.EmailLogAdminResponse;
import org.mizoguchi.misaki.pojo.vo.admin.ExceptionLogAdminResponse;
import org.mizoguchi.misaki.service.admin.LogAdminService;
import org.springframework.data.util.ParsingUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
@Tag(name = "管理端-日志相关接口")
public class LogAdminController {
    private final LogAdminService logAdminService;

    @Operation(summary = "分页条件搜索邮件日志")
    @PostMapping("/email/search")
    public Result<List<EmailLogAdminResponse>> searchEmailLogs(@RequestParam @Positive Integer pageIndex,
                                                               @RequestParam @Positive Integer pageSize,
                                                               @RequestParam(required = false) String sortField,
                                                               @RequestParam(defaultValue = "asc") String sortOrder,
                                                               @RequestBody @Validated SearchEmailLogAdminRequest searchEmailLogAdminRequest){
        if (sortField != null && !sortField.isBlank()){
            try {
                User.class.getDeclaredField(sortField);
            } catch (NoSuchFieldException e) {
                throw new InvalidSortParamsException(FailMessageConstant.INVALID_SORT_PARAMS);
            }
            sortField = ParsingUtils.reconcatenateCamelCase(sortField, "_");
        }
        return Result.success(logAdminService.searchEmailLogs(pageIndex, pageSize, sortField, sortOrder, searchEmailLogAdminRequest));
    }

    @Operation(summary = "删除某日期及以前的邮件日志")
    @DeleteMapping("/email")
    public Result<Void> deleteEmailLogs(@RequestParam LocalDate date){
        logAdminService.deleteEmailLogs(date);
        return Result.success();
    }

    @Operation(summary = "分页条件搜索异常日志")
    @PostMapping("/exception/search")
    public Result<List<ExceptionLogAdminResponse>> searchExceptionLogs(@RequestParam @Positive Integer pageIndex,
                                                                       @RequestParam @Positive Integer pageSize,
                                                                       @RequestParam(required = false) String sortField,
                                                                       @RequestParam(defaultValue = "asc") String sortOrder,
                                                                       @RequestBody @Validated SearchExceptionLogAdminRequest searchExceptionLogAdminRequest){
        if (sortField != null && !sortField.isBlank()){
            try {
                User.class.getDeclaredField(sortField);
            } catch (NoSuchFieldException e) {
                throw new InvalidSortParamsException(FailMessageConstant.INVALID_SORT_PARAMS);
            }
        }
        return Result.success(logAdminService.searchExceptionLogs(pageIndex, pageSize, sortField, sortOrder, searchExceptionLogAdminRequest));
    }

    @Operation(summary = "删除某日期及以前的异常日志")
    @DeleteMapping("/exception")
    public Result<Void> deleteExceptionLogs(@RequestParam LocalDate date){
        logAdminService.deleteExceptionLogs(date);
        return Result.success();
    }
}
