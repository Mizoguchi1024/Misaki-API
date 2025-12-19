package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class WrongVerificationCodeException extends BaseException {
    public WrongVerificationCodeException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, 42220, message);
    }
}
