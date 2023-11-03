package com.example.axon

import com.example.axon.Quantity.Companion.ONE
import com.example.axon.Quantity.Companion.THREE
import com.example.axon.Quantity.Companion.TWO
import com.example.axon.Quantity.Companion.ZERO
import com.example.axon.command.SellableCommands.Acquire
import com.example.axon.command.SellableCommands.Fill
import com.example.axon.event.SellableEvents.Acquired
import com.example.axon.event.SellableEvents.Depleted
import com.example.axon.event.SellableEvents.Filled
import io.kotest.matchers.shouldBe
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.jupiter.api.Test

class SellableProductTest {
    private var fixture: FixtureConfiguration<SellableProduct> =
        AggregateTestFixture(SellableProduct::class.java)

    @Test
    fun `create a product`() {
        fixture.givenNoPriorActivity()
            .`when`(
                Fill(
                    id = aProductId,
                    name = aProductName,
                    amount = Money.of("10 EUR"),
                    quantity = ONE
                )
            )
            .expectEvents(
                Filled(
                    productId = aProductId,
                    name = aProductName,
                    amount = Money.of("10 EUR"),
                    quantity = ONE
                )
            )
            .expectState {
                it.quantity shouldBe ONE
            }
    }

    @Test
    fun `acquire a product`() {
        fixture.given(
            Filled(
                productId = aProductId,
                name = aProductName,
                amount = Money.of("10 EUR"),
                quantity = THREE
            )
        ).`when`(
            Acquire(
                id = aProductId,
                quantity = ONE
            )
        ).expectEvents(
            Acquired(
                productId = aProductId,
                remainingQuantity = TWO
            )
        ).expectState {
            it.quantity shouldBe TWO
        }
    }

    @Test
    fun `acquire a product and only one is remaining`() {
        fixture.given(
            Filled(
                productId = aProductId,
                name = aProductName,
                amount = Money.of("10 EUR"),
                quantity = TWO
            )
        ).`when`(
            Acquire(
                id = aProductId,
                quantity = ONE
            )
        ).expectEvents(
            Acquired(
                productId = aProductId,
                remainingQuantity = ONE
            ),
            Depleted(
                productId = aProductId
            )
        ).expectState {
            it.quantity shouldBe ONE
        }
    }

    @Test
    fun `acquire a product no more available`() {
        fixture.given(
            Filled(
                productId = aProductId,
                name = aProductName,
                amount = Money.of("10 EUR"),
                quantity = ZERO
            )
        ).`when`(
            Acquire(
                id = aProductId,
                quantity = ONE
            )
        ).expectNoEvents()
            .expectException(IllegalStateException::class.java)
    }

    companion object {
        private val aProductId = ProductId.new()
        private val aProductName = "a-product-name".toProductName()
    }
}