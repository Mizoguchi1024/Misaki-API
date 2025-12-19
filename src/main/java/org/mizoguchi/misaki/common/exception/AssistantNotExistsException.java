package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class AssistantNotExistsException extends BaseException {
    public AssistantNotExistsException(String message) {
        super(HttpStatus.NOT_FOUND, 40403, message);
    }
}
