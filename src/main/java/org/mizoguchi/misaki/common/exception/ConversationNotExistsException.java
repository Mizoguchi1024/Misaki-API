package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ConversationNotExistsException extends BaseException {
    public ConversationNotExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
