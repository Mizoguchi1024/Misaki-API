package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ExceptionLogNotExistsException extends BaseException {
    public ExceptionLogNotExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
