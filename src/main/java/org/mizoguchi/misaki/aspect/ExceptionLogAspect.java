package org.mizoguchi.misaki.aspect;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.mizoguchi.misaki.annotation.EnableExceptionLog;
import org.mizoguchi.misaki.mapper.ExceptionLogMapper;
import org.mizoguchi.misaki.pojo.entity.ExceptionLog;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionLogAspect {
    @Resource
    private ExceptionLogMapper exceptionLogMapper;

    @AfterReturning("@annotation(enableExceptionLog)")
    public void insertExceptionLog(JoinPoint joinPoint, EnableExceptionLog enableExceptionLog) {
        Object[] args = joinPoint.getArgs();

        Throwable e = null;
        HttpServletRequest request = null;

        for (Object arg : args) {
            if (arg instanceof Throwable) {
                e = (Throwable) arg;
            } else if (arg instanceof HttpServletRequest) {
                request = (HttpServletRequest) arg;
            }
        }

        if (e == null || request == null) {
            return;
        }

        ExceptionLog exceptionLog = ExceptionLog.builder()
                .exception(e.getClass().getSimpleName())
                .message(e.getMessage() == null ? "" : e.getMessage())
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .method(request.getMethod())
                .build();

        exceptionLogMapper.insert(exceptionLog);
    }
}
