package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class WishNotExistsException extends BaseException {
    public WishNotExistsException(String message) {
        super(HttpStatus.NOT_FOUND, 40406, message);
    }
}
