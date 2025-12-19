package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class AtLeastOneAssistantException extends BaseException {
    public AtLeastOneAssistantException(String message) {
        super(HttpStatus.FORBIDDEN, 40302, message);
    }
}
