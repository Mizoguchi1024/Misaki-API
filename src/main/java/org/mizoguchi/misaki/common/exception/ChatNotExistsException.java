package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ChatNotExistsException extends BaseException {
    public ChatNotExistsException(String message) {
        super(HttpStatus.NOT_FOUND, 40402, message);
    }
}
