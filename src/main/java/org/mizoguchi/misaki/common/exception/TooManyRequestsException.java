package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class TooManyRequestsException extends BaseException {
    public TooManyRequestsException(String message) {
        super(HttpStatus.TOO_MANY_REQUESTS, 42900, message);
    }
}
