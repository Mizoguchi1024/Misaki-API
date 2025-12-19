package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ModelNotExistsException extends BaseException {
    public ModelNotExistsException(String message) {
        super(HttpStatus.NOT_FOUND, 40404, message);
    }
}
