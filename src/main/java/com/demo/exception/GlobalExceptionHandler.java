package com.demo.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * http://www.baeldung.com/exception-handling-for-rest-with-spring
 *
 * @author Lubomir Simeonov
 *
 */
/**
 * @author WeiJia
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<Object> handleBasicException(Exception ex,
      WebRequest request) {

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String body = "Internal ERROR";
    if(ex.getMessage()!=null) {
      body = ex.getMessage();
    }
    return this.handleExceptionInternal(ex, body, new HttpHeaders(), status, request);
  }

}
