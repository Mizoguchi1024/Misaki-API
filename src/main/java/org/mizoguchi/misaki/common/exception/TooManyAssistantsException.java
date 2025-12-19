package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class TooManyAssistantsException extends BaseException {
    public TooManyAssistantsException(String message) {
        super(HttpStatus.TOO_MANY_REQUESTS, 42901, message);
    }
}
