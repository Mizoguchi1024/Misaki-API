package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class EmailLogNotExistsException extends BaseException {
  public EmailLogNotExistsException(String message) {
    super(HttpStatus.BAD_REQUEST, 400, message);
  }
}
