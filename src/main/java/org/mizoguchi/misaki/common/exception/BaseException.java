package org.mizoguchi.misaki.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final int code;
    private final String message;

    public BaseException(HttpStatus status, int code, String message) {
        super(message);
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
