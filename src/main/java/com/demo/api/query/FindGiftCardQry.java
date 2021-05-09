package com.demo.api.query;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindGiftCardQry implements Serializable {
  private String id;
}
