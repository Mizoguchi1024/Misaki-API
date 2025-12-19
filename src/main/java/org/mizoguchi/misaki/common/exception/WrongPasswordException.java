package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class WrongPasswordException extends BaseException {
    public WrongPasswordException(String message) {
        super(HttpStatus.UNAUTHORIZED, 40101, message);
    }
}
