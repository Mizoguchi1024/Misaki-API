package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ChatTitleAlreadyExistsException extends BaseException {
    public ChatTitleAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, 40902, message);
    }
}
