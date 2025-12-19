package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class TokenNotEnoughException extends BaseException {
    public TokenNotEnoughException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, 42210, message);
    }
}
