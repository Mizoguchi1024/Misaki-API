package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class CrystalNotEnoughException extends BaseException {
    public CrystalNotEnoughException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, 42211, message);
    }
}
