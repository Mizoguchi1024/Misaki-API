package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class WishNotExistsException extends BaseException {
    public WishNotExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
