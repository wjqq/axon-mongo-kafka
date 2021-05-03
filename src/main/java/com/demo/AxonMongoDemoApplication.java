package com.demo;

import java.util.Collections;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.MongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoFactory;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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
                .messageMonitor(configuration.messageMonitor(EventStore.class, "eventStore"))
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

  //////////////////////////////////////////////////////////////////////////////////////

  @Bean
  public MongoTemplate axonMongoTemplate() {
    return DefaultMongoTemplate.builder().mongoDatabase(mongo(), "db-axon").build();
  }

  @Bean
  public MongoClient mongo() {
    MongoFactory mongoFactory = new MongoFactory();
    mongoFactory
        .setMongoAddresses(Collections.singletonList(new ServerAddress("127.0.0.1", 27017)));
    return mongoFactory.createMongo();
  }
}
