/**
 * 
 */
package com.demo.config;

import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.extensions.mongo.MongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.serialization.Serializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 025937672
 *
 */
@Configuration
@AutoConfigureAfter(MongoConfig.class)
public class AxonConfig {

  // The method name "giftCardSnapConfig" is referred in the aggregation class.
  @Bean
  public SnapshotTriggerDefinition giftCardSnapConfig(
      org.axonframework.config.Configuration configuration) {
    return new EventCountSnapshotTriggerDefinition(configuration.snapshotter(), 5);
  }


  @Bean
  public EventStorageEngine storageEngine(MongoTemplate template) {
    return MongoEventStorageEngine.builder().mongoTemplate(template).build();
  }

  // https://github.com/AxonFramework/extension-mongo/blob/master/mongo-axon-example/src/main/kotlin/org/axonframework/extension/mongo/example/MongoAxonExampleApplication.kt
  @Bean
  public TokenStore tokenStore(MongoTemplate template, Serializer serializer) {
    return MongoTokenStore.builder().mongoTemplate(template)
       .serializer(serializer).build();
  }
}
