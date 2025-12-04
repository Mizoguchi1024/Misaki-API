package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class AlreadyCheckInException extends BaseException {
    public AlreadyCheckInException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
