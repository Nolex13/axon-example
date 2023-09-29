package com.example.axon

import com.example.axon.CartCommands.Finalize
import com.example.axon.CartEvents.PurchaseRequired
import com.example.axon.Quantity.Companion.ONE
import com.example.axon.Quantity.Companion.ZERO
import com.example.axon.SellableCommands.Acquire
import com.example.axon.SellableEvents.Acquired
import org.axonframework.test.saga.SagaTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType

class CartSagaTest {
    private val jdbcTemplate = NamedParameterJdbcTemplate(
        EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .build(),
    )

    private val fixture = SagaTestFixture(CartSaga::class.java).apply {
        registerResource(jdbcTemplate)
    }

    @BeforeEach
    fun setUp() {
        jdbcTemplate.update(
            """INSERT INTO Cart (cartId, productId, quantity) VALUES (:cardId, :productId, :quantity)""",
            mapOf("cardId" to aCartId.id, "productId" to aProductId.id, "quantity" to 1)
        )
        jdbcTemplate.update(
            """INSERT INTO Cart (cartId, productId, quantity) VALUES (:cardId, :productId, :quantity)""",
            mapOf("cardId" to aCartId.id, "productId" to anotherProductId.id, "quantity" to 1)
        )
    }

    @Test
    fun `on purchase required`() {
        fixture.givenNoPriorActivity()
            .whenPublishingA(
                PurchaseRequired(aCartId)
            )
            .expectNoScheduledDeadlines()
            .expectDispatchedCommands(
                Acquire(
                    id = aProductId,
                    quantity = ONE
                ),
                Acquire(
                    id = anotherProductId,
                    quantity = ONE
                ),
            )
            .expectActiveSagas(1)
    }

    @Test
    fun `on only one product acquired`() {
        fixture.givenAPublished(
            PurchaseRequired(aCartId)
        ).whenAggregate(aProductId.id)
            .publishes(
                Acquired(
                    productId = aProductId,
                    remainingQuantity = ZERO
                )
            )
            .expectNoScheduledDeadlines()
            .expectNoDispatchedCommands()
            .expectActiveSagas(1)
    }

    @Test
    fun `on all products acquired`() {
        fixture.givenAggregate(aCartId.id)
            .published(
                PurchaseRequired(aCartId),
            ).andThenAggregate(aProductId.id)
            .published(
                Acquired(
                    productId = aProductId,
                    remainingQuantity = ZERO
                )
            )
            .whenAggregate(anotherProductId.id)
            .publishes(
                Acquired(
                    productId = anotherProductId,
                    remainingQuantity = ZERO
                )
            )
            .expectNoScheduledDeadlines()
            .expectDispatchedCommands(Finalize(aCartId))
            .expectActiveSagas(0)
    }

    companion object {
        private val aCartId = CartId.new()
        private val aProductId = "a-product-code".toProductId()
        private val anotherProductId = "another-product-code".toProductId()
    }
}