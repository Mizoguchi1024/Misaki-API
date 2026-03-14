package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class CannotDeleteYourselfException extends BaseException {
    public CannotDeleteYourselfException(String message) {
        super(HttpStatus.FORBIDDEN, 40303, message);
    }
}