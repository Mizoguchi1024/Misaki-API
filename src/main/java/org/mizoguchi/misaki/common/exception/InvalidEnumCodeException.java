package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidEnumCodeException extends BaseException {
    public InvalidEnumCodeException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
