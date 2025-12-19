package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidSortParamsException extends BaseException {
    public InvalidSortParamsException(String message) {
        super(HttpStatus.BAD_REQUEST, 40013, message);
    }
}
