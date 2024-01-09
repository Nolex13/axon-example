package com.example.axon.aggregate

import com.example.axon.PurchaseOrderCode
import com.example.axon.VariationCode
import com.example.axon.command.PurchaseOrderCommands.Create
import com.example.axon.command.PurchaseOrderCommands.CreateVariation
import com.example.axon.command.PurchaseOrderCommands.FixVariation
import com.example.axon.event.PurchaseOrderEvents.Created
import com.example.axon.event.PurchaseOrderEvents.VariationCreated
import com.example.axon.event.PurchaseOrderEvents.VariationFailed
import com.example.axon.event.PurchaseOrderEvents.VariationFixed
import kotlin.random.Random
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class PurchaseOrder() {
    @AggregateIdentifier
    private lateinit var purchaseOrderCode: PurchaseOrderCode

    private val failingVariation: MutableMap<VariationCode, CreateVariation> = mutableMapOf()

    @CommandHandler
    constructor(
        command: Create,
    ) : this() {
        AggregateLifecycle.apply(
            Created(
                purchaseOrderCode = command.purchaseOrderCode
            ),
        )
    }

    @EventSourcingHandler
    fun on(event: Created) {
        purchaseOrderCode = event.purchaseOrderCode
    }

    @CommandHandler
    fun on(command: CreateVariation) {
        if (failingVariation.isNotEmpty() || isNotValid()) {
            AggregateLifecycle.apply(
                VariationFailed(
                    purchaseOrderCode = command.purchaseOrderCode,
                    commandPayload = command,
                )
            )
        } else {
            AggregateLifecycle.apply(
                VariationCreated(
                    purchaseOrderCode = command.purchaseOrderCode,
                    variationCode = command.variationCode,
                    type = command.type,
                )
            )
        }
    }

    @EventSourcingHandler
    fun on(event: VariationFailed) {
        failingVariation.put(event.commandPayload.variationCode, event.commandPayload)
    }

    @CommandHandler
    fun on(command: FixVariation) {
        AggregateLifecycle.apply(
            VariationFixed(
                purchaseOrderCode = command.purchaseOrderCode,
                variationCode = command.variationCode,
                type = command.type,
            )
        )
    }

    @EventSourcingHandler
    fun on(event: VariationFixed) {
        failingVariation.remove(event.variationCode)
    }

    @EventSourcingHandler
    fun on(event: VariationCreated) {
        failingVariation.remove(event.variationCode)
    }

    private fun isNotValid() = Random.nextInt(1) == 1
}
