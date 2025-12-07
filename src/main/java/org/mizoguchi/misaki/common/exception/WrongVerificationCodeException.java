package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class WrongVerificationCodeException extends BaseException {
    public WrongVerificationCodeException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
