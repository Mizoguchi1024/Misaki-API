package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class WrongPasswordException extends BaseException {
    public WrongPasswordException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
