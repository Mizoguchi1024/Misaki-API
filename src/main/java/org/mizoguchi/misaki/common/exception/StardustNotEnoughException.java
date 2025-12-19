package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class StardustNotEnoughException extends BaseException {
    public StardustNotEnoughException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, 42212, message);
    }
}
