package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class AccountTemporarilyLockedException extends BaseException {
    public AccountTemporarilyLockedException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
