package org.mizoguchi.misaki.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.constant.SqlConstant;
import org.mizoguchi.misaki.common.exception.EmailLogNotExistsException;
import org.mizoguchi.misaki.common.exception.ExceptionLogNotExistsException;
import org.mizoguchi.misaki.common.result.PageResult;
import org.mizoguchi.misaki.mapper.EmailLogMapper;
import org.mizoguchi.misaki.mapper.ExceptionLogMapper;
import org.mizoguchi.misaki.pojo.dto.admin.SearchEmailLogAdminRequest;
import org.mizoguchi.misaki.pojo.dto.admin.SearchExceptionLogAdminRequest;
import org.mizoguchi.misaki.pojo.entity.EmailLog;
import org.mizoguchi.misaki.pojo.entity.ExceptionLog;
import org.mizoguchi.misaki.pojo.vo.admin.EmailLogAdminResponse;
import org.mizoguchi.misaki.pojo.vo.admin.ExceptionLogAdminResponse;
import org.mizoguchi.misaki.service.admin.LogAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogAdminServiceImpl implements LogAdminService {
    private final EmailLogMapper emailLogMapper;
    private final ExceptionLogMapper exceptionLogMapper;


    @Override
    public PageResult<EmailLogAdminResponse> searchEmailLogs(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchEmailLogAdminRequest searchEmailLogAdminRequest) {
        IPage<EmailLog> emailLogsPage = emailLogMapper.selectPage(new Page<>(pageIndex, pageSize), new QueryWrapper<EmailLog>()
                .orderBy(sortField != null, sortOrder.equalsIgnoreCase(SqlConstant.ASC), sortField)
                .lambda()
                .like(searchEmailLogAdminRequest.getId() != null, EmailLog::getId, searchEmailLogAdminRequest.getId())
                .like(searchEmailLogAdminRequest.getSender() != null, EmailLog::getSender, searchEmailLogAdminRequest.getSender())
                .like(searchEmailLogAdminRequest.getReceiver() != null, EmailLog::getReceiver, searchEmailLogAdminRequest.getReceiver())
                .like(searchEmailLogAdminRequest.getSubject() != null, EmailLog::getSubject, searchEmailLogAdminRequest.getSubject())
                .like(searchEmailLogAdminRequest.getCreateTime() != null, EmailLog::getCreateTime, searchEmailLogAdminRequest.getCreateTime())
        );

        PageResult<EmailLogAdminResponse> pageResult = new PageResult<>();
        pageResult.setList(emailLogsPage.getRecords().stream()
                .map(emailLog -> {
                    EmailLogAdminResponse emailLogAdminResponse = new EmailLogAdminResponse();
                    BeanUtils.copyProperties(emailLog, emailLogAdminResponse);

                    return emailLogAdminResponse;
                }).collect(Collectors.toList()));

        pageResult.setTotal(emailLogsPage.getTotal());
        pageResult.setPageIndex(emailLogsPage.getCurrent());
        pageResult.setPageSize(emailLogsPage.getSize());

        return pageResult;
    }

    @Override
    public void deleteEmailLogs(LocalDate date) {
        int affectedRows = emailLogMapper.delete(new LambdaQueryWrapper<EmailLog>()
                .le(EmailLog::getCreateTime, date)
        );

        if (affectedRows == 0) {
            throw new EmailLogNotExistsException(FailMessageConstant.EMAIL_LOG_NOT_EXISTS);
        }
    }

    @Override
    public PageResult<ExceptionLogAdminResponse> searchExceptionLogs(Integer pageIndex, Integer pageSize, String sortField, String sortOrder, SearchExceptionLogAdminRequest searchExceptionLogAdminRequest) {
        IPage<ExceptionLog> exceptionLogsPage = exceptionLogMapper.selectPage(new Page<>(pageIndex, pageSize), new QueryWrapper<ExceptionLog>()
                .orderBy(sortField != null, sortOrder.equalsIgnoreCase(SqlConstant.ASC), sortField)
                .lambda()
                .like(searchExceptionLogAdminRequest.getId() != null, ExceptionLog::getId, searchExceptionLogAdminRequest.getId())
                .like(searchExceptionLogAdminRequest.getException() != null, ExceptionLog::getException, searchExceptionLogAdminRequest.getException())
                .like(searchExceptionLogAdminRequest.getMessage() != null, ExceptionLog::getMessage, searchExceptionLogAdminRequest.getMessage())
                .like(searchExceptionLogAdminRequest.getIp() != null, ExceptionLog::getIp, searchExceptionLogAdminRequest.getIp())
                .like(searchExceptionLogAdminRequest.getUri() != null, ExceptionLog::getUri, searchExceptionLogAdminRequest.getUri())
                .eq(searchExceptionLogAdminRequest.getMethod() != null, ExceptionLog::getMethod, searchExceptionLogAdminRequest.getMethod())
                .like(searchExceptionLogAdminRequest.getCreateTime() != null, ExceptionLog::getCreateTime, searchExceptionLogAdminRequest.getCreateTime())
        );
        
        PageResult<ExceptionLogAdminResponse> pageResult = new PageResult<>();
        pageResult.setList(exceptionLogsPage.getRecords().stream()
                .map(exceptionLog -> {
                    ExceptionLogAdminResponse exceptionLogAdminResponse = new ExceptionLogAdminResponse();
                    BeanUtils.copyProperties(exceptionLog, exceptionLogAdminResponse);

                    return exceptionLogAdminResponse;
                }).collect(Collectors.toList()));

        pageResult.setTotal(exceptionLogsPage.getTotal());
        pageResult.setPageIndex(exceptionLogsPage.getCurrent());
        pageResult.setPageSize(exceptionLogsPage.getSize());

        return pageResult;
    }

    @Override
    public void deleteExceptionLogs(LocalDate date) {
        int affectedRows = exceptionLogMapper.delete(new LambdaQueryWrapper<ExceptionLog>()
                .le(ExceptionLog::getCreateTime, date)
        );

        if (affectedRows == 0) {
            throw new ExceptionLogNotExistsException(FailMessageConstant.EXCEPTION_NOT_EXISTS);
        }
    }
}
