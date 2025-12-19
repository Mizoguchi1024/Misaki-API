package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ModelNotExistsException extends BaseException {
    public ModelNotExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
