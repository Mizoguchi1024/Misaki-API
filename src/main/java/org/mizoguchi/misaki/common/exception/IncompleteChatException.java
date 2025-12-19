package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class IncompleteChatException extends BaseException {
    public IncompleteChatException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, 42202, message);
    }
}
