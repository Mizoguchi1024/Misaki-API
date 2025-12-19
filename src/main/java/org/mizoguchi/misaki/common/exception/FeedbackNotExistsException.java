package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class FeedbackNotExistsException extends BaseException {
    public FeedbackNotExistsException(String message) {
        super(HttpStatus.NOT_FOUND, 40405, message);
    }
}
