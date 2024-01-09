package com.example.axon.saga

import com.example.axon.command.PurchaseOrderCommands.CreateVariation
import com.example.axon.event.PurchaseOrderEvents
import com.example.axon.event.PurchaseOrderEvents.VariationFailed
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga

@Saga
class PurchaseOrderSaga{
    private val failingVariation: MutableList<CreateVariation> = mutableListOf()

    @StartSaga
    @SagaEventHandler(associationProperty = "purchaseOrderCode")
    fun on(event: VariationFailed) {
        failingVariation.add(event.commandPayload)
    }

    @SagaEventHandler(associationProperty = "purchaseOrderCode")
    fun on(
        event: PurchaseOrderEvents.VariationFixed,
        commandGateway: CommandGateway
    ) {
        failingVariation.forEach {
            commandGateway.send<Unit>(it)
        }
        SagaLifecycle.end()
    }
}