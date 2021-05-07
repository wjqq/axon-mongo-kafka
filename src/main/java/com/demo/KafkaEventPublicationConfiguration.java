/**
 * 
 */
package com.demo;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.extensions.kafka.eventhandling.producer.ConfirmationMode;
import org.axonframework.extensions.kafka.eventhandling.producer.DefaultProducerFactory;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaEventPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.ProducerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 025937672
 *
 */
@Configuration
public class KafkaEventPublicationConfiguration {
  
  @Value("${axon.kafka.consumer.bootstrapservers}")
  String bootstrapservers;
  
  String topic = "t111111111111111";
  
  @Autowired
  public void registerPublisherToEventProcessor(EventProcessingConfigurer eventProcessingConfigurer,
      KafkaEventPublisher<String, byte[]> kafkaEventPublisher) {
    String processingGroup = KafkaEventPublisher.DEFAULT_PROCESSING_GROUP;
    eventProcessingConfigurer.registerEventHandler(configuration -> kafkaEventPublisher)
        .assignHandlerTypesMatching(processingGroup,
            clazz -> clazz.isAssignableFrom(KafkaEventPublisher.class))
        .registerTrackingEventProcessor(processingGroup);
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
        .configuration(kafkaConfig())
        .confirmationMode(ConfirmationMode.WAIT_FOR_ACK)
        .build();
  }

  private Map<String, Object> kafkaConfig() {
    Map<String, Object> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapservers);
    config.put(ProducerConfig.RETRIES_CONFIG, 0);
    config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
    config.put(ProducerConfig.LINGER_MS_CONFIG, 1);
    config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        org.apache.kafka.common.serialization.StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
    return config;
  }
}
