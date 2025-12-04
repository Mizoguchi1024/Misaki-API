package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ModelNotExistsExption extends BaseException {
    public ModelNotExistsExption(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
