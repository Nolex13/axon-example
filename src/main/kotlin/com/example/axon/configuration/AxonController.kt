package com.example.axon.configuration

import java.util.concurrent.CompletableFuture
import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.TrackingEventProcessor
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AxonController(
    private val eventProcessingConfiguration: EventProcessingConfiguration,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/axon/admin/processing-groups/{processingGroup}/replay")
    @Transactional
    fun replay(@PathVariable("processingGroup") processingGroup: String): ResponseEntity<Void> {
        return eventProcessingConfiguration.eventProcessor(
            processingGroup,
            TrackingEventProcessor::class.java,
        )
            .map {
                logger.warn("Resetting processing group {} - {}", processingGroup, it)
                it.shutDown()
                it.resetTokens()
                it.start()
                ResponseEntity.noContent().build<Void>()
            }
            .orElse(ResponseEntity.notFound().build())
    }

    @PostMapping("/axon/admin/processing-groups/{processingGroup}/split/{segmentId}")
    fun split(
        @PathVariable("processingGroup") processingGroup: String,
        @PathVariable("segmentId") segmentId: Int,
    ) =
        eventProcessingConfiguration
            .eventProcessor(processingGroup, TrackingEventProcessor::class.java)
            .map { eventProcessor ->
                eventProcessor.splitSegment(segmentId).thenApply {
                    if (it) {
                        eventProcessor.start()
                        ResponseEntity.accepted().build<Unit>()
                    } else {
                        ResponseEntity.internalServerError().build()
                    }
                }
            }.orElse(
                CompletableFuture.supplyAsync { ResponseEntity.notFound().build() }
            )

    @PostMapping("/axon/admin/processing-groups/{processingGroup}/merge/{segmentId}")
    fun merge(
        @PathVariable("processingGroup") processingGroup: String,
        @PathVariable("segmentId") segmentId: Int,
    ) =
        eventProcessingConfiguration
            .eventProcessor(processingGroup, TrackingEventProcessor::class.java)
            .map {
                it.releaseSegment(segmentId)
                it.mergeSegment(segmentId)
                ResponseEntity.accepted().build<Unit>()
            }.orElse(
                ResponseEntity.notFound().build(),
            )
}
