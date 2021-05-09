package com.demo.query.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("GiftCard")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiftCardEntity {
    @Id
    private String id;
    private Integer initialValue;
    private Integer remainingValue;
}
