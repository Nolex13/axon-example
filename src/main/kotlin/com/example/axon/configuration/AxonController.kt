package com.example.axon.configuration

import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.TrackingEventProcessor
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AxonController(
    private val eventProcessingConfiguration: EventProcessingConfiguration,
    private val jdbcTemplate: NamedParameterJdbcTemplate,
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

    @GetMapping(
        value = ["/axon/admin/processing-groups/{processingGroup}/owner"],
        produces = ["text/plain"],
    )
    fun getOwner(@PathVariable("processingGroup") processingGroup: String): ResponseEntity<String> =
        try {
            val owner = jdbcTemplate.queryForObject(
                """
                    SELECT owner FROM TokenEntry WHERE processorName = :processorName
                """,
                mapOf("processorName" to processingGroup),
                String::class.java,
            )!!
            ResponseEntity.ok(owner.substringAfter('@'))
        } catch (e: Exception) {
            logger.warn("Cannot retrieve owner of token {}", processingGroup, e)
            ResponseEntity.notFound().build()
        }
}
