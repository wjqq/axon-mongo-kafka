/**
 * 
 */
package com.demo.config;

import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.MongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * @author 025937672
 *
 */
@Configuration
public class MongoConfig {
  @Bean
  public MongoTemplate axonMongoTemplate() {
    return DefaultMongoTemplate.builder().mongoDatabase(mongo(), "db-axon0").build();
  }

  @Bean
  public MongoClient mongo() {
    return new MongoClient(new ServerAddress("localhost", 27017));
  }

  @Bean
  public MongoDbFactory mongoDbFactory(MongoClient client) {
    return new SimpleMongoDbFactory(client, "db-axon0");
  }

}
