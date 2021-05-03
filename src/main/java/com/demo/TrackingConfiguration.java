/**
 * 
 */
package com.demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.extensions.kafka.eventhandling.consumer.AsyncFetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.DefaultConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.KafkaEventMessage;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.StreamableKafkaMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 025937672
 *
 */
@Configuration
@ConditionalOnProperty(name = "axon.kafka.consumer.event-processor-mode",
    havingValue = "TRACKING")
public class TrackingConfiguration {
  @Autowired
  public void configureStreamableKafkaSource(EventProcessingConfigurer configurer,
      StreamableKafkaMessageSource<String, byte[]> streamableKafkaMessageSource) {
    configurer.registerTrackingEventProcessor("kafka-group-1",
        config -> streamableKafkaMessageSource);
  }

  @Bean
  public StreamableKafkaMessageSource<String, byte[]> streamableKafkaMessageSource(
      ConsumerFactory<String, byte[]> consumerFactory,
      Fetcher<String, byte[], KafkaEventMessage> fetcher) {
    return StreamableKafkaMessageSource.<String, byte[]>builder().topics(Arrays.asList("T1")) // Defaults
        .consumerFactory(consumerFactory) // Hard requirement
        .fetcher(fetcher) // Hard requirement
        .build();
  }

  @Bean
  public Fetcher<?, ?, ?> fetcher() {
    return AsyncFetcher.builder() // Defaults to "5000" milliseconds
        .build();
  }

  @Bean
  public ConsumerFactory<String, byte[]> consumerFactory() {

    Map<String, Object> config = new HashMap<>();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        "localhost:9092");
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(ConsumerConfig.GROUP_ID_CONFIG, "G1");

    return new DefaultConsumerFactory<>(config);
  }
}
