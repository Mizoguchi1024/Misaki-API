package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ChatTitleAlreadyExistsException extends BaseException {
    public ChatTitleAlreadyExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
