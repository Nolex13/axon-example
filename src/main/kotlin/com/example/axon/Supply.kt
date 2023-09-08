package com.example.axon

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate


@Aggregate
class Supply() {
    @AggregateIdentifier
    private lateinit var productId: ProductId

    @CommandHandler
    constructor(
        command: SupplyCommands.Buy,
    ) : this() {
        AggregateLifecycle.apply(
            SupplyEvents.Bought(
                productId = command.id,
                name = command.name,
                amount = command.amount
            ),
        )
    }

    @EventSourcingHandler
    fun on(event: SupplyEvents.Bought) {
        productId = event.productId
    }
}