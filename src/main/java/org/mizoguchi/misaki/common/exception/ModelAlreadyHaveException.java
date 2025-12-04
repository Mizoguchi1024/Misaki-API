package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ModelAlreadyHaveException extends BaseException {
    public ModelAlreadyHaveException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
