package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ModelAlreadyOwnedException extends BaseException {
    public ModelAlreadyOwnedException(String message) {
        super(HttpStatus.CONFLICT, 40903, message);
    }
}
