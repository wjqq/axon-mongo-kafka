package com.demo.api.event;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class IssuedEvt implements Serializable {
  private String id;
  private Integer amount;
}
