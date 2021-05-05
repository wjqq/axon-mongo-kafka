package com.demo.domain;

import com.demo.api.command.CardIncreaseCmd;
import com.demo.api.command.IssueCmd;
import com.demo.api.event.CardIncreasedEvt;
import com.demo.api.event.IssuedEvt;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.invoke.MethodHandles;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@NoArgsConstructor
@Aggregate(snapshotTriggerDefinition="giftCardSnapConfig")
class GiftCard {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @AggregateIdentifier
    private String id;
    private int remainingValue;

    @CommandHandler
    GiftCard(IssueCmd cmd) {
    log.info("handling {}", cmd);
        if (cmd.getAmount() <= 0) throw new IllegalArgumentException("amount <= 0");
        apply(new IssuedEvt(cmd.getId(), cmd.getAmount()));
    }
    
    @CommandHandler
    public void updateCardIncrease(CardIncreaseCmd cmd) {
        log.info("handling {}", cmd);
        apply(new CardIncreasedEvt(cmd.getId(), cmd.getAmount()));
    }

    @EventSourcingHandler
    void on(IssuedEvt evt) {
      log.info("applying {}", evt);
          id = evt.getId();
          remainingValue = evt.getAmount();
      log.info("new remaining value: {}", remainingValue);
    }
    
    @EventSourcingHandler
    void on(CardIncreasedEvt evt) {
      remainingValue = this.remainingValue + evt.getAmount();
      log.info("new remaining value: {}", remainingValue);
    }
}
