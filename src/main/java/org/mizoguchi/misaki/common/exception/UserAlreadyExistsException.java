package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseException {
    public UserAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, 40901, message);
    }
}
