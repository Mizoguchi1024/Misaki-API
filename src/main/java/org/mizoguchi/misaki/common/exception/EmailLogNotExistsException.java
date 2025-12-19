package org.mizoguchi.misaki.common.exception;

import org.springframework.http.HttpStatus;

public class EmailLogNotExistsException extends BaseException {
  public EmailLogNotExistsException(String message) {
    super(HttpStatus.NOT_FOUND, 40407, message);
  }
}
