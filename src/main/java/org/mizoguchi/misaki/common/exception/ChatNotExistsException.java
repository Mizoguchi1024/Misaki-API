package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ChatNotExistsException extends BaseException {
    public ChatNotExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
