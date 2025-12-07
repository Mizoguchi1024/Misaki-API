package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class FailedToSendEmailException extends BaseException {
    public FailedToSendEmailException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, 500, message);
    }
}
