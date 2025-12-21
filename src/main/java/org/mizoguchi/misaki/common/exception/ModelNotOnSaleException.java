package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class ModelNotOnSaleException extends BaseException {
    public ModelNotOnSaleException(String message) {
        super(HttpStatus.BAD_REQUEST, 40014, message);
    }
}
