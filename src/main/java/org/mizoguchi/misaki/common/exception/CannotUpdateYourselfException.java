package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class CannotUpdateYourselfException extends BaseException {
    public CannotUpdateYourselfException(String message) {
        super(HttpStatus.FORBIDDEN, 40304, message);
    }
}