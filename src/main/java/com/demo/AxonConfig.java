/**
 * 
 */
package com.demo;

import org.axonframework.config.Configurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.messaging.StreamableMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author 025937672
 *
 */
@Configuration
public class AxonConfig {

  @Autowired
  public void configureProcessor(Configurer configurer) {
    configurer.eventProcessing().usingSubscribingEventProcessors()
        .registerTrackingEventProcessor("GiftCardHandler2",
            org.axonframework.config.Configuration::eventStore,
            configuration -> TrackingEventProcessorConfiguration.forParallelProcessing(1)
                .andInitialTrackingToken(StreamableMessageSource::createHeadToken))
        .registerTrackingEventProcessor("GiftCardHandler",
            org.axonframework.config.Configuration::eventStore,
            configuration -> TrackingEventProcessorConfiguration.forParallelProcessing(2)
                .andInitialTrackingToken(StreamableMessageSource::createHeadToken));
  }
}
