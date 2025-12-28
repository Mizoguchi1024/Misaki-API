package org.mizoguchi.misaki.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.mizoguchi.misaki.annotation.EnableExceptionLog;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理自定义业务异常
     */
    @EnableExceptionLog
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Void>> handleCustomException(BaseException e, HttpServletRequest request) {
        log.warn("Exception={} | IP={} | URI={} | Method={} | Message={}",
                e.getClass().getSimpleName(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getMessage());

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

        log.warn("Exception={} | IP={} | URI={} | Method={} | Message={}",
                e.getClass().getSimpleName(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), fullMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(40000, fullMessage));
    }

    /**
     * 处理 @RequestParam / @PathVariable 等参数校验异常
     */
    @EnableExceptionLog
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations()
                .stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse(FailMessageConstant.INVALID_PARAMETER);

        log.warn("Exception={} | IP={} | URI={} | Method={} | Message={}",
                e.getClass().getSimpleName(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(40000, message));
    }

    /**
     * 处理 RequestParam 异常
     */
    @EnableExceptionLog
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Result<Void>> handleRequestParameterException(Exception e, HttpServletRequest request){
        log.warn("Exception={} | IP={} | URI={} | Method={} | Message={}",
                e.getClass().getSimpleName(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(40000, e.getMessage()));
    }

    /**
     * 处理非法参数异常
     */
    @EnableExceptionLog
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("Exception={} | IP={} | URI={} | Method={} | Message={}",
                e.getClass().getSimpleName(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(40000, e.getMessage()));
    }

    /**
     * 处理404异常
     */
    @EnableExceptionLog
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request){
        log.warn("Exception={} | IP={} | URI={} | Method={} | Message={}",
                e.getClass().getSimpleName(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), FailMessageConstant.RESOURCE_NOT_FOUND);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.fail(40400, FailMessageConstant.RESOURCE_NOT_FOUND));
    }

    /**
     * 处理流式输出中断异常
     */
    @EnableExceptionLog
    @ExceptionHandler({AsyncRequestNotUsableException.class, ClientAbortException.class, IOException.class})
    public void handleClientAbortException(Exception e, HttpServletRequest request) {
        log.warn("Exception={} | IP={} | URI={} | Method={} | Message={}",
                e.getClass().getSimpleName(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), FailMessageConstant.CLIENT_DISCONNECTED);
    }

    /**
     * 处理其他异常
     */
    @EnableExceptionLog
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e, HttpServletRequest request) {
        log.error("Exception={} | IP={} | URI={} | Method={} | Message={}",
                e.getClass().getSimpleName(), request.getRemoteAddr(), request.getRequestURI(), request.getMethod(), FailMessageConstant.INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.fail(50000, FailMessageConstant.INTERNAL_SERVER_ERROR));
    }
}
