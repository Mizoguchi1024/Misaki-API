package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class TodayAlreadyCheckInException extends BaseException {
    public TodayAlreadyCheckInException(String message) {
        super(HttpStatus.BAD_REQUEST, 400, message);
    }
}
