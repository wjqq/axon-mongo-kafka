package com.demo.query;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.demo.api.FindAllGiftCard;
import com.demo.api.FindGiftCardQry;
import com.demo.api.GiftCardRecord;
import com.demo.api.IssuedEvt;
import com.demo.query.repo.GiftCardRepository;

@Component
class GiftCardHandler {

  private final GiftCardRepository giftCardRepository;
  private final QueryUpdateEmitter queryUpdateEmitter;


  @Autowired
  org.springframework.data.mongodb.core.MongoTemplate mt;

  @Autowired
  public GiftCardHandler(GiftCardRepository giftCardRepository,
      QueryUpdateEmitter queryUpdateEmitter) {
    this.giftCardRepository = giftCardRepository;
    this.queryUpdateEmitter = queryUpdateEmitter;
  }

  @EventHandler
  void on(IssuedEvt event) {

    List<GiftCardEntity> beforeActions = giftCardRepository.findAll();

    /*
     * Update our read model by inserting the new card.
     */
    giftCardRepository
        .save(new GiftCardEntity(event.getId(), event.getAmount(), event.getAmount()));

    /* Send it to subscription queries of type FindGiftCardQry, but only if the card id matches. */
    queryUpdateEmitter.emit(FindGiftCardQry.class,
        findGiftCardQry -> Objects.equals(event.getId(), findGiftCardQry.getId()),
        new GiftCardRecord(event.getId(), event.getAmount(), event.getAmount()));
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
