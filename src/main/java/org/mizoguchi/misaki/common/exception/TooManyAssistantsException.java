package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class TooManyAssistantsException extends BaseException {
    public TooManyAssistantsException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
