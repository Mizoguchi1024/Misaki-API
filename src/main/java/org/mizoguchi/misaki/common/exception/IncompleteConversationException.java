package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class IncompleteConversationException extends BaseException {
    public IncompleteConversationException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
