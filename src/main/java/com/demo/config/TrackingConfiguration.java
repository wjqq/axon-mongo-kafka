/**
 * 
 */
package com.demo.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.axonframework.config.Configurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.extensions.kafka.eventhandling.consumer.AsyncFetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.DefaultConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.KafkaEventMessage;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.StreamableKafkaMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(AxonConfig.class)
public class TrackingConfiguration {
  
  @Value("${axon.kafka.bootstrapservers}")
  String bootstrapservers;
  @Value("${axon.kafka.consumer.group-id}")
  String groupId;
  @Value("${axon.kafka.consumer.topic}")
  String topic;
  
  @Autowired
  public void configureProcessor(Configurer configurer,
      StreamableKafkaMessageSource<String, byte[]> streamableKafkaMessageSource,
      TokenStore tokenStore) {
    
    configurer.eventProcessing().usingSubscribingEventProcessors()
        .registerTrackingEventProcessor("GiftCardHandler",
            c->streamableKafkaMessageSource,
            c -> TrackingEventProcessorConfiguration.forParallelProcessing(1)
            //Don't need to intialize the token as Axon should cover it for us..
                .andTokenClaimInterval(1000, TimeUnit.SECONDS));
  }
  
  @Bean
  public StreamableKafkaMessageSource<String, byte[]> streamableKafkaMessageSource(
      ConsumerFactory<String, byte[]> consumerFactory,
      Fetcher<String, byte[], KafkaEventMessage> fetcher) {
    return StreamableKafkaMessageSource.<String, byte[]>builder()
        .topics(Arrays.asList(topic)) // Defaults
        .consumerFactory(consumerFactory) // Hard requirement
        .fetcher(fetcher) // Hard requirement
        .build();
  }
  
  @Bean
  public Fetcher<String, byte[], KafkaEventMessage> fetcher() {
    return AsyncFetcher.<String, byte[], KafkaEventMessage>builder() // Defaults to "5000" milliseconds
        .build();
  }
  
  @Bean
  public ConsumerFactory<String, byte[]> consumerFactory() {
    Map<String, Object> config = new HashMap<>();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapservers);
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    System.out.println("bootstrapservers: "+bootstrapservers);
    System.out.println("groupId: "+groupId);
    return new DefaultConsumerFactory<>(config);
  }
}
