package org.mizoguchi.misaki.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
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
    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<Result<Void>> handleCustomException(BaseException e, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        log.warn("{} | IP={} | URI={} | Method={} | Exception={}",
                e.getMessage(), ip, uri, method, e.getClass().getSimpleName());

        return ResponseEntity.status(e.getStatus()).body(Result.fail(e.getCode(), e.getMessage()));
    }

    /**
     * 处理 @RequestBody 校验失败异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : FailMessageConstant.INVALID_PARAMETER;
        String field = e.getBindingResult().getFieldError().getField();
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        log.warn("{} | Field={} | IP={} | URI={} | Method={} | Exception={}",
                message, field, ip, uri, method, e.getClass().getSimpleName());

        return ResponseEntity.status(400).body(Result.fail(400, message + "：" + field));
    }

    /**
     * 处理 @RequestParam / @PathVariable 等参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations()
                .stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse(FailMessageConstant.INVALID_PARAMETER);
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        log.warn("{} | IP={} | URI={} | Method={} | Exception={}",
                message, ip, uri, method, e.getClass().getSimpleName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.fail(400, message));
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request){
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        log.warn("{} | IP={} | URI={} | Method={} | Exception={} | Message={}",
                FailMessageConstant.NOT_FOUND, ip, uri, method, e.getClass().getSimpleName(), e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.fail(404, FailMessageConstant.NOT_FOUND));
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        log.error("{} | IP={} | URI={} | Method={} | Exception={} | Message={}",
                FailMessageConstant.INTERNAL_SERVER_ERROR, ip, uri, method, e.getClass().getSimpleName(), e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.fail(500, FailMessageConstant.INTERNAL_SERVER_ERROR));
    }
}
