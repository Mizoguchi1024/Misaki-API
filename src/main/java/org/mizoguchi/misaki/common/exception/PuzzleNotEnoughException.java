package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class PuzzleNotEnoughException extends BaseException {
    public PuzzleNotEnoughException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, 42213, message);
    }
}
