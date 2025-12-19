package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ExceptionLogNotExistsException extends BaseException {
    public ExceptionLogNotExistsException(String message) {
        super(HttpStatus.NOT_FOUND, 40408, message);
    }
}
