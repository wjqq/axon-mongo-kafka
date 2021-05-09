/**
 * 
 */
package com.demo.api.command;

import java.io.Serializable;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This command is used increase the balance of the specified card
 * 
 * @author 025937672
 *
 */
@AllArgsConstructor
@Data
public class CardIncreaseCmd  implements Serializable {
  @TargetAggregateIdentifier
  private String id;
  private Integer amount;
}
