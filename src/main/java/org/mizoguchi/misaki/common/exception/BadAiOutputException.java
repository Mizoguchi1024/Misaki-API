package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class BadAiOutputException extends BaseException {
    public BadAiOutputException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, 500, message);
    }
}
