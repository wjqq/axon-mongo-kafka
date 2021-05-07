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
@ProcessingGroup("GiftCardHandler")
class GiftCardHandler {

  private final GiftCardRepository giftCardRepository;
  private final QueryUpdateEmitter queryUpdateEmitter;

  @Autowired
  public GiftCardHandler(GiftCardRepository giftCardRepository,
      QueryUpdateEmitter queryUpdateEmitter) {
    this.giftCardRepository = giftCardRepository;
    this.queryUpdateEmitter = queryUpdateEmitter;
  }

  @EventHandler
  void on(IssuedEvt event) {
    
    System.out.println("Insued. "+ event.getId() +">>"+ Thread.currentThread().getName());

    /*
     * Update our read model by inserting the new card.
     */
    giftCardRepository
        .save(new GiftCardEntity(event.getId(), event.getAmount(), event.getAmount()));

    /* Send it to subscription queries of type FindGiftCardQry, but only if the card id matches. */
    queryUpdateEmitter.emit(FindGiftCardQry.class,
        findGiftCardQry -> Objects.equals(event.getId(), findGiftCardQry.getId()),
        new GiftCardRecord(event.getId(), event.getAmount(), event.getAmount()));
  
    System.out.println("Insued Emitter. "+ event.getId());
  }
  
  @EventHandler
  void on(CardIncreasedEvt event) throws Exception {
    
    GiftCardEntity entity = giftCardRepository.findById(event.getId()).orElse(null);
    entity.setRemainingValue(entity.getRemainingValue()+event.getAmount());
    giftCardRepository.save(entity);

    /* Send it to subscription queries of type FindGiftCardQry, but only if the card id matches. */
    queryUpdateEmitter.emit(FindGiftCardQry.class,
        findGiftCardQry -> Objects.equals(event.getId(), findGiftCardQry.getId()),
        new GiftCardRecord(event.getId(), entity.getInitialValue(), entity.getRemainingValue()));
 
    System.out.println("Increased Emitter. "+ event.getId());
  }
  
  @QueryHandler
  GiftCardRecord handle(FindGiftCardQry query) {
    GiftCardEntity giftCardEntity =
        giftCardRepository.findById(query.getId()).orElse(new GiftCardEntity());
    return new GiftCardRecord(giftCardEntity.getId(), giftCardEntity.getInitialValue(),
        giftCardEntity.getRemainingValue());
  }

  @QueryHandler
  List<GiftCardRecord> handle(FindAllGiftCard query) {
    List<GiftCardEntity> giftCardEntities = giftCardRepository.findAll();
    return giftCardEntities.stream()
        .map(giftCardEntity -> new GiftCardRecord(giftCardEntity.getId(),
            giftCardEntity.getInitialValue(), giftCardEntity.getRemainingValue()))
        .collect(Collectors.toList());
  }
}
