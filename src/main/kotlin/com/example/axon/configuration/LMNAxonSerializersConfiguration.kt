package com.example.axon.configuration

import com.fasterxml.jackson.databind.DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class LMNAxonSerializersConfiguration {
    @Bean
    fun eventSerializer(
        @Qualifier("axonSerializerObjectMapper") axonSerializerObjectMapper: ObjectMapper,
    ): Serializer =
        JacksonSerializer.builder().objectMapper(axonSerializerObjectMapper).build()

    @Primary
    @Bean
    fun generalSerializer(
        @Qualifier("axonSerializerObjectMapper") axonSerializerObjectMapper: ObjectMapper,
    ): Serializer =
        JacksonSerializer.builder().objectMapper(axonSerializerObjectMapper).build()

    @Primary
    @Bean
    fun axonSerializerObjectMapper(): ObjectMapper =
        jacksonObjectMapper()
            .findAndRegisterModules()
            .configure(WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
}
