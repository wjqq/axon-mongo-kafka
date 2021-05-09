package com.demo.web.req;

import java.io.Serializable;
import lombok.Data;

@Data
public class CardIncreaseRqst implements Serializable {
  private String id;
  private Integer value;
}
