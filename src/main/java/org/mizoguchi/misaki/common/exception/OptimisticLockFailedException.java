package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class OptimisticLockFailedException extends BaseException {
    public OptimisticLockFailedException(String message) {
        super(HttpStatus.CONFLICT, 40900, message);
    }
}
