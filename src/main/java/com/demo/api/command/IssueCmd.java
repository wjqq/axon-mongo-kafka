package com.demo.api.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@AllArgsConstructor
@Data
public class IssueCmd implements Serializable {

    @TargetAggregateIdentifier
    private String id;
    private Integer amount;

}
