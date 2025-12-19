package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class AlreadyCheckedInException extends BaseException {
    public AlreadyCheckedInException(String message) {
        super(HttpStatus.CONFLICT, 40904, message);
    }
}
