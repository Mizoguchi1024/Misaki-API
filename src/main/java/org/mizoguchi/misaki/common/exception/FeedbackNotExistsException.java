package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class FeedbackNotExistsException extends BaseException {
    public FeedbackNotExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
