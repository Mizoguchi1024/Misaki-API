package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ModelNotOwnedException extends BaseException {
    public ModelNotOwnedException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
