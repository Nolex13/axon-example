package com.example.axon.configuration

import org.axonframework.config.EventProcessingConfigurer
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration


@Configuration
class AxonConfig {
    @Autowired
    fun autoConfigureEventProcessors(
        configurer: EventProcessingConfigurer,
    ) {
        val tepConfig =
            TrackingEventProcessorConfiguration.forParallelProcessing(4)
                .andInitialSegmentsCount(4)

        configurer.registerTrackingEventProcessorConfiguration(
            "CartSagaProcessor"
        ) { config: org.axonframework.config.Configuration? -> tepConfig }
    }
}