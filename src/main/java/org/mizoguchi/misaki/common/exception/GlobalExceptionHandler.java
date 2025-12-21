package org.mizoguchi.misaki.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.mizoguchi.misaki.annotation.EnableExceptionLog;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理自定义业务异常
     */
    @EnableExceptionLog
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Void>> handleCustomException(BaseException e, HttpServletRequest request) {
        log.warn("{} | IP={} | URI={} | Method={} | Exception={}",
                e.getMessage(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getClass().getSimpleName());

        return ResponseEntity.status(e.getStatus()).body(Result.fail(e.getCode(), e.getMessage()));
    }

    /**
     * 处理 @RequestBody 校验失败异常
     */
    @EnableExceptionLog
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : FailMessageConstant.INVALID_PARAMETER;
        String field = e.getBindingResult().getFieldError().getField();
        String fullMessage = message + ": " + field;

        log.warn("{} | IP={} | URI={} | Method={} | Exception={}",
                fullMessage, request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getClass().getSimpleName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(40000, fullMessage));
    }

    /**
     * 处理 @RequestParam / @PathVariable 等参数校验异常
     */
    @EnableExceptionLog
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations()
                .stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse(FailMessageConstant.INVALID_PARAMETER);

        log.warn("{} | IP={} | URI={} | Method={} | Exception={}",
                message, request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getClass().getSimpleName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(40000, message));
    }

    /**
     * 处理非法参数异常
     */
    @EnableExceptionLog
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("{} | IP={} | URI={} | Method={} | Exception={}",
                e.getMessage(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(40000, e.getMessage()));
    }

    /**
     * 处理404异常
     */
    @EnableExceptionLog
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request){
        log.warn("{} | IP={} | URI={} | Method={} | Exception={}",
                FailMessageConstant.RESOURCE_NOT_FOUND, request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getClass().getSimpleName());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.fail(40400, FailMessageConstant.RESOURCE_NOT_FOUND));
    }

    /**
     * 处理其他异常
     */
    @EnableExceptionLog
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e, HttpServletRequest request) {
        log.error("{} | IP={} | URI={} | Method={} | Exception={}",
                FailMessageConstant.INTERNAL_SERVER_ERROR, request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getClass().getSimpleName());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.fail(50000, FailMessageConstant.INTERNAL_SERVER_ERROR));
    }
}
