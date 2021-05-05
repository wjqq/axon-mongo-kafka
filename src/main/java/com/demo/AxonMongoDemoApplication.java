package com.demo;

import org.axonframework.config.Configuration;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.MongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@SpringBootApplication
public class AxonMongoDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AxonMongoDemoApplication.class, args);
    }

    /**
     * Axon provides an event store `EmbeddedEventStore`. It delegates actual storage and retrieval of events to an `EventStorageEngine`.
     *
     * @param storageEngine
     * @param configuration
     * @return EmbeddedEventStore
     */
    @Bean
    public EmbeddedEventStore eventStore(EventStorageEngine storageEngine, AxonConfiguration configuration) {
        return EmbeddedEventStore.builder()
                .storageEngine(storageEngine)
        .messageMonitor(configuration.messageMonitor(EventStore.class, "eventStore1"))
                .build();
    }

    /**
     * The MongoEventStorageEngine stores each event in a separate MongoDB document
     *
     * @param client
     * @return EventStorageEngine
     */
  @Bean
  // com.mongodb.client.MongoClient->com.mongodb.MongoClient
  public EventStorageEngine storageEngine(MongoTemplate template) {
    return MongoEventStorageEngine.builder()
        .mongoTemplate(template)
        .build();
    }

  // https://github.com/AxonFramework/extension-mongo/blob/master/mongo-axon-example/src/main/kotlin/org/axonframework/extension/mongo/example/MongoAxonExampleApplication.kt
  @Bean
  public TokenStore tokenStore(MongoTemplate template, Serializer serializer) {
    return MongoTokenStore.builder()
        .mongoTemplate(template)
        .serializer(serializer).build();
  }

  /**
   * Configures a snapshot trigger to create a Snapshot every 5 events.
   * 5 is an arbitrary number used only for testing purposes just to show how the snapshots are stored on Mongo as well.
   */
  @Bean
  public SnapshotTriggerDefinition giftCardSnapConfig(Configuration configuration) {
    return new EventCountSnapshotTriggerDefinition(configuration.snapshotter(), 5);
  }

  @Bean
  public MongoTemplate axonMongoTemplate() {
    return DefaultMongoTemplate.builder().mongoDatabase(mongo(), "db-axon0").build();
  }

  @Bean
  public MongoDbFactory mongoDbFactory(MongoClient client) {
    return new SimpleMongoDbFactory(client, "db-axon0");
  }

  @Bean
  public MongoClient mongo() {
    return new MongoClient(new ServerAddress("localhost", 27017));
  }
}
