package com.demo.web;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.demo.api.command.CardIncreaseCmd;
import com.demo.api.command.IssueCmd;
import com.demo.api.query.FindAllGiftCard;
import com.demo.api.query.FindGiftCardQry;
import com.demo.api.request.CardIncreaseRqst;
import com.demo.api.request.IssueRqst;
import com.demo.api.response.GiftCardRecord;

/**
 * Repository REST Controller for handling 'commands' only
 * <p>
 * Sometimes you may want to write a custom handler for a specific resource. To take advantage of Spring Data REST’s settings, message converters, exception handling, and more, we use the @RepositoryRestController annotation instead of a standard Spring MVC @Controller or @RestController
 */
@RestController
public class AxonMongoDemoRestController {

  @Autowired
  private CommandGateway commandGateway;
  @Autowired
  private QueryGateway queryGateway;

  @PatchMapping(value = "/cards", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> cardIncreaseRequest(@RequestBody CardIncreaseRqst request) {

    try (SubscriptionQueryResult<GiftCardRecord, GiftCardRecord> queryResult =
        queryGateway.subscriptionQuery(new FindGiftCardQry(request.getId()),
            ResponseTypes.instanceOf(GiftCardRecord.class),
            ResponseTypes.instanceOf(GiftCardRecord.class))) {

      commandGateway.sendAndWait(new CardIncreaseCmd(request.getId(), request.getValue()));

      /* Returning the first update sent to our find card query. */
      GiftCardRecord giftCardRecord = queryResult.updates().blockFirst();
      return ResponseEntity.ok().body(giftCardRecord);
    }
 
  }
  
  @PostMapping(value = "/cards", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> cards(@RequestBody IssueRqst request) {

    final String giftCardId = UUID.randomUUID().toString();

    try (SubscriptionQueryResult<GiftCardRecord, GiftCardRecord> queryResult =
        queryGateway.subscriptionQuery(new FindGiftCardQry(giftCardId),
            ResponseTypes.instanceOf(GiftCardRecord.class),
            ResponseTypes.instanceOf(GiftCardRecord.class))) {

      commandGateway.sendAndWait(new IssueCmd(giftCardId, request.getValue()));

      /* Returning the first update sent to our find card query. */
      GiftCardRecord giftCardRecord = queryResult.updates().blockFirst();
      return ResponseEntity.ok().body(giftCardRecord);
    }
  }

  @GetMapping(value = "/cards/{cardId}")
  public CompletableFuture<GiftCardRecord> cards(@PathVariable String cardId) {
    return queryGateway.query(new FindGiftCardQry(cardId),
        ResponseTypes.instanceOf(GiftCardRecord.class));
  }

  @GetMapping(value = "/cards")
  public CompletableFuture<List<GiftCardRecord>> allCards() {
    return queryGateway.query(new FindAllGiftCard(),
        ResponseTypes.multipleInstancesOf(GiftCardRecord.class));
  }

}
