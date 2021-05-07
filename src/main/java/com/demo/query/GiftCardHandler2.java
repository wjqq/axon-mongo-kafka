package com.demo.query;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.demo.api.event.CardIncreasedEvt;
import com.demo.api.event.IssuedEvt;
import com.demo.api.query.FindAllGiftCard;
import com.demo.api.query.FindGiftCardQry;
import com.demo.api.response.GiftCardRecord;
import com.demo.domain.exception.NotEnoughMoneyException;
import com.demo.query.entity.GiftCardEntity;
import com.demo.query.repo.GiftCardRepository;

@Component
@ProcessingGroup("GiftCardHandler2")
class GiftCardHandler2 {

  @EventHandler
  void on(IssuedEvt event) {
    System.out.println("Insued in 2. "+ event.getId());
  }
}
