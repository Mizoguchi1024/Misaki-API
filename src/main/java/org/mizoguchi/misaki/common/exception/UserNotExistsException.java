package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class UserNotExistsException extends BaseException {
    public UserNotExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
