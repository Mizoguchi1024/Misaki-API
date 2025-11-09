package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class WrongVerifyCodeException extends BaseException {
    public WrongVerifyCodeException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
