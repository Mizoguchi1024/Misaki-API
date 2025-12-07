package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ModelAlreadyOwnedException extends BaseException {
    public ModelAlreadyOwnedException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
