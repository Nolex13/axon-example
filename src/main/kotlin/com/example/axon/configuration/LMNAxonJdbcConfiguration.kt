package com.example.axon.configuration

import org.axonframework.common.jdbc.ConnectionProvider
import org.axonframework.common.jdbc.PersistenceExceptionResolver
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.eventhandling.tokenstore.TokenStore
import org.axonframework.eventhandling.tokenstore.jdbc.JdbcTokenStore
import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine
import org.axonframework.modelling.saga.repository.SagaStore
import org.axonframework.modelling.saga.repository.jdbc.JdbcSagaStore
import org.axonframework.serialization.Serializer
import org.axonframework.springboot.autoconfig.JdbcAutoConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@AutoConfigureBefore(JdbcAutoConfiguration::class)
@AutoConfigureAfter(DataSourceAutoConfiguration::class, LMNAxonSerializersConfiguration::class)
class LMNAxonJdbcConfiguration {
    @Bean
    fun eventStorageEngine(
        generalSerializer: Serializer,
        persistenceExceptionResolver: PersistenceExceptionResolver,
        @Qualifier("eventSerializer") eventSerializer: Serializer,
        configuration: org.axonframework.config.Configuration,
        connectionProvider: ConnectionProvider,
        transactionManager: TransactionManager,
    ): EventStorageEngine =
        JdbcEventStorageEngine
            .builder()
            .snapshotSerializer(generalSerializer)
            .upcasterChain(configuration.upcasterChain())
            .persistenceExceptionResolver(persistenceExceptionResolver)
            .eventSerializer(eventSerializer)
            .snapshotFilter(configuration.snapshotFilter())
            .connectionProvider(connectionProvider)
            .transactionManager(transactionManager)
            .build()

    @Bean
    @ConditionalOnMissingBean
    fun tokenStore(
        connectionProvider: ConnectionProvider,
        generalSerializer: Serializer,
    ): TokenStore =
        JdbcTokenStore
            .builder()
            .connectionProvider(connectionProvider)
            .serializer(generalSerializer).build()

    @Bean
    @ConditionalOnMissingBean(SagaStore::class)
    fun sagaStore(
        connectionProvider: ConnectionProvider,
        generalSerializer: Serializer,
    ): JdbcSagaStore =
        JdbcSagaStore
            .builder()
            .connectionProvider(connectionProvider)
            .serializer(generalSerializer)
            .build()
}
