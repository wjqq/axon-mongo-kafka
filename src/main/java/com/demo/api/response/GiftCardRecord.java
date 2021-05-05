package com.demo.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GiftCardRecord {
  private String id;
  private Integer initialValue;
  private Integer remainingValue;
}
