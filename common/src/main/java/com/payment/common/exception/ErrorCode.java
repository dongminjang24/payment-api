package com.payment.common.exception;

import org.springframework.http.HttpStatus;



public interface ErrorCode {
  HttpStatus getHttpStatus();
  String getDetail();
}
