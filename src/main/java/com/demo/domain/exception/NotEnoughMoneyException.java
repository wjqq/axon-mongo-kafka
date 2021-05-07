/**
 * 
 */
package com.demo.domain.exception;

import org.axonframework.commandhandling.CommandExecutionException;

/**
 * @author 025937672
 *
 */
public class NotEnoughMoneyException extends CommandExecutionException{

  public NotEnoughMoneyException(String message) {
    super(message, null, null);
  }
  
  public NotEnoughMoneyException(String message, Throwable cause) {
    super(message, cause, null);
  }
}
