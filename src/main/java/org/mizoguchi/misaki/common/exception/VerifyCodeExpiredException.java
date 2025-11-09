package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class VerifyCodeExpiredException extends BaseException {
    public VerifyCodeExpiredException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
