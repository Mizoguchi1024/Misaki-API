package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class VerificationCodeExpiredException extends BaseException {
    public VerificationCodeExpiredException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, 42221, message);
    }
}
