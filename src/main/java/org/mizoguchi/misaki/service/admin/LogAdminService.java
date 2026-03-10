package org.mizoguchi.misaki.service.admin;

import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.pojo.dto.admin.SearchEmailLogAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchExceptionLogAdminRequest;
import org.mizoguchi.misaki.pojo.vo.admin.EmailLogAdminResponse;
import org.mizoguchi.misaki.pojo.vo.admin.ExceptionLogAdminResponse;

import java.time.LocalDate;

public interface LogAdminService {
    PageResult<EmailLogAdminResponse> searchEmailLogs(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchEmailLogAdminRequest searchEmailLogAdminRequest);
    void deleteEmailLogs(LocalDate date);
    PageResult<ExceptionLogAdminResponse> searchExceptionLogs(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchExceptionLogAdminRequest searchExceptionLogAdminRequest);
    void deleteExceptionLogs(LocalDate date);
}
