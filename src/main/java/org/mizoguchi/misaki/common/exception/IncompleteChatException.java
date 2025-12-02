package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class IncompleteChatException extends BaseException {
    public IncompleteChatException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
