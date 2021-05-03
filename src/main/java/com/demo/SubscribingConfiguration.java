/**
 * 
 */
package com.demo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author 025937672
 *
 */
@Configuration
@ConditionalOnProperty(name = "axon.kafka.consumer.event-processor-mode",
    havingValue = "SUBSCRIBING")
public class SubscribingConfiguration {
  //
  // @Bean
  // public KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer() {
  // return new KafkaMessageSourceConfigurer();
  // }
  //
  // @Autowired
  // public void kafkaMessageSourceConfigurer(Configurer configurer,
  // KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer) {
  // configurer.registerModule(kafkaMessageSourceConfigurer);
  // }
  //
  // @Bean
  // public SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource(
  // List<String> topics, String groupId, ConsumerFactory<String, byte[]> consumerFactory,
  // Fetcher<String, byte[], EventMessage<?>> fetcher,
  // KafkaMessageConverter<String, byte[]> messageConverter, int consumerCount,
  // KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer) {
  // SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource =
  // SubscribableKafkaMessageSource.<String, byte[]>builder().topics(topics) // Defaults to a
  // // collection of
  // // "Axon.Events"
  // .groupId(groupId) // Hard requirement
  // .consumerFactory(consumerFactory) // Hard requirement
  // .fetcher(fetcher) // Hard requirement
  // .messageConverter(messageConverter) // Defaults to a "DefaultKafkaMessageConverter"
  // .consumerCount(consumerCount) // Defaults to a single Consumer
  // .build();
  // // Registering the source is required to tie into the Configurers lifecycle to start the source
  // // at the right stage
  // kafkaMessageSourceConfigurer
  // .registerSubscribableSource(configuration -> subscribableKafkaMessageSource);
  // return subscribableKafkaMessageSource;
  // }
  //
  // @Autowired
  // public void configureSubscribableKafkaSource(EventProcessingConfigurer
  // eventProcessingConfigurer,
  // String processorName,
  // SubscribableKafkaMessageSource<String, byte[]> subscribableKafkaMessageSource) {
  // eventProcessingConfigurer.registerSubscribingEventProcessor(processorName,
  // configuration -> subscribableKafkaMessageSource);
  // }
}
