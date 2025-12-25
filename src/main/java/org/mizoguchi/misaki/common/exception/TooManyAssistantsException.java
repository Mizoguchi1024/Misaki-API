package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class TooManyAssistantsException extends BaseException {
    public TooManyAssistantsException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, 42202, message);
    }
}
