package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class AlreadyCheckedInException extends BaseException {
    public AlreadyCheckedInException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
