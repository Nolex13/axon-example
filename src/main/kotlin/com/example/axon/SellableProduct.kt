package com.example.axon

import com.example.axon.Quantity.Companion.ONE
import com.example.axon.Quantity.Companion.ZERO
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate


@Aggregate
class SellableProduct() {
    @AggregateIdentifier
    private lateinit var productId: ProductId
    private var quantity: Quantity = ZERO

    @CommandHandler
    constructor(
        command: SellableCommands.Fill,
    ) : this() {
        AggregateLifecycle.apply(
            SellableEvents.Filled(
                productId = command.id,
                name = command.name,
                amount = command.amount,
                quantity = command.quantity
            ),
        )
    }

    @EventSourcingHandler
    fun on(event: SellableEvents.Filled) {
        productId = event.productId
        quantity = event.quantity
    }

    @CommandHandler
    fun on(command: SellableCommands.Acquire) {
        val remainingQuantity = quantity - command.quantity

        check(remainingQuantity >= ZERO)

        AggregateLifecycle.apply(
            SellableEvents.Acquired(
                productId = command.id,
                quantity = remainingQuantity
            ),
        )

        if (remainingQuantity == ONE) {
            AggregateLifecycle.apply(SellableEvents.Depleted(command.id))
        }
    }

    @EventSourcingHandler
    fun on(event: SellableEvents.Acquired) {
        quantity = Quantity(quantity.amount - event.quantity.amount)
    }
}