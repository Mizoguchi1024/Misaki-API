package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class EnabledAssistantDeletionException extends BaseException {
    public EnabledAssistantDeletionException(String message) {
        super(HttpStatus.CONFLICT, 40905, message);
    }
}
