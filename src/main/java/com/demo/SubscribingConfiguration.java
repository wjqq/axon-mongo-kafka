/**
 * 
 */
package com.demo;

import java.util.List;
import org.axonframework.config.Configurer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.extensions.kafka.configuration.KafkaMessageSourceConfigurer;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.subscribable.SubscribableKafkaMessageSource;
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
    havingValue = "SUBSCRIBING")
public class SubscribingConfiguration {

  @Bean
  public KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer() {
    return new KafkaMessageSourceConfigurer();
  }

  @Autowired
  public void kafkaMessageSourceConfigurer(Configurer configurer,
      KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer) {
    configurer.registerModule(kafkaMessageSourceConfigurer);
  }

  @Bean
  public SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource(
      List<String> topics, String groupId, ConsumerFactory<String, byte[]> consumerFactory,
      Fetcher<String, byte[], EventMessage<?>> fetcher,
      KafkaMessageConverter<String, byte[]> messageConverter, int consumerCount,
      KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer) {
    SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource =
        SubscribableKafkaMessageSource.<String, byte[]>builder().topics(topics) // Defaults to a
                                                                                // collection of
                                                                                // "Axon.Events"
            .groupId(groupId) // Hard requirement
            .consumerFactory(consumerFactory) // Hard requirement
            .fetcher(fetcher) // Hard requirement
            .messageConverter(messageConverter) // Defaults to a "DefaultKafkaMessageConverter"
            .consumerCount(consumerCount) // Defaults to a single Consumer
            .build();
    // Registering the source is required to tie into the Configurers lifecycle to start the source
    // at the right stage
    kafkaMessageSourceConfigurer
        .registerSubscribableSource(configuration -> subscribableKafkaMessageSource);
    return subscribableKafkaMessageSource;
  }

  @Autowired
  public void configureSubscribableKafkaSource(EventProcessingConfigurer eventProcessingConfigurer,
      String processorName,
      SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource) {
    eventProcessingConfigurer.registerSubscribingEventProcessor(processorName,
        configuration -> subscribableKafkaMessageSource);
  }
}
