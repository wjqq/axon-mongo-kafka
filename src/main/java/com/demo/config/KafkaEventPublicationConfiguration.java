/**
 * 
 */
package com.demo.config;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.extensions.kafka.eventhandling.producer.ConfirmationMode;
import org.axonframework.extensions.kafka.eventhandling.producer.DefaultProducerFactory;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaEventPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.ProducerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 025937672
 * 
 * https://github.com/AxonFramework/extension-kafka/blob/8f1a0322bf14d146f8055f5771be2937342db752/kafka/src/test/java/org/axonframework/extensions/kafka/eventhandling/util/ProducerConfigUtil.java#L152
 * https://github.com/AxonFramework/extension-kafka/blob/master/kafka/src/test/java/org/axonframework/extensions/kafka/eventhandling/KafkaIntegrationTest.java
 *
 */
@Configuration
public class KafkaEventPublicationConfiguration {

  private String bootstrapServer = "localhost:9092";
  private String topic = "mytopic-axon-1";

  @Autowired
  public void registerPublisherToEventProcessor(EventProcessingConfigurer eventProcessingConfigurer,
      KafkaEventPublisher<String, byte[]> kafkaEventPublisher) {
    String processingGroup = KafkaEventPublisher.DEFAULT_PROCESSING_GROUP;
    eventProcessingConfigurer.registerEventHandler(configuration -> kafkaEventPublisher)
        .assignHandlerTypesMatching(processingGroup,
            clazz -> clazz.isAssignableFrom(KafkaEventPublisher.class))
        .registerSubscribingEventProcessor(processingGroup);
    // Replace `registerSubscribingEventProcessor` for `registerTrackingEventProcessor` to use a
    // tracking processor
  }

  @Bean
  public KafkaEventPublisher<String, byte[]> kafkaEventPublisher(
      KafkaPublisher<String, byte[]> kafkaPublisher) {
    return KafkaEventPublisher.<String, byte[]>builder().kafkaPublisher(kafkaPublisher)
        .build();
  }

  @Bean
  public KafkaPublisher<String, byte[]> kafkaPublisher(
      ProducerFactory<String, byte[]> producerFactory) {
    return KafkaPublisher.<String, byte[]>builder().topic(topic).producerFactory(producerFactory)
        .build();
  }

  @Bean
  public ProducerFactory<String, byte[]> producerFactory() {
    return DefaultProducerFactory.<String, byte[]>builder().closeTimeout(1000, ChronoUnit.MILLIS)
        .producerCacheSize(10000)
        .configuration(kafkaConfig(bootstrapServer,
            org.apache.kafka.common.serialization.ByteArraySerializer.class))
        .confirmationMode(ConfirmationMode.WAIT_FOR_ACK)
        .build();
  }

  private static Map<String, Object> kafkaConfig(String bootstrapServer, Class<?> valueSerializer) {
    Map<String, Object> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    config.put(ProducerConfig.RETRIES_CONFIG, 0);
    config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
    config.put(ProducerConfig.LINGER_MS_CONFIG, 1);
    config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        org.apache.kafka.common.serialization.StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
    return config;
  }
}
