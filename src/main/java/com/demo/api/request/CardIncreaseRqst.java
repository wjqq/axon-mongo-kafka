package com.demo.api.request;

import java.io.Serializable;
import lombok.Data;

@Data
public class CardIncreaseRqst implements Serializable {
  private String id;
  private Integer value;
}

