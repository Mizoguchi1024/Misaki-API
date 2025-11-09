package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class SendingEmailFailedException extends BaseException {
    public SendingEmailFailedException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, 500, message);
    }
}
