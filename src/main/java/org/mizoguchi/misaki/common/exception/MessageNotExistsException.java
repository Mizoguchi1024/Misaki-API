package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class MessageNotExistsException extends BaseException {
    public MessageNotExistsException(String message) {
        super(HttpStatus.NOT_FOUND, 40409, message);
    }
}
