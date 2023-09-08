package com.example.axon

import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga

data class SupplyProduct(
    val product: Product,
    val isBought: Boolean = false
){
    fun bought(): SupplyProduct =
        SupplyProduct(product, true)
}

@Saga
class CartSaga {
    lateinit var cartId: CartId

    private var products: List<SupplyProduct> = emptyList()

    @StartSaga
    @SagaEventHandler(associationProperty = "cartId")
    fun on(event: CartEvents.Created) {
        this.cartId = event.cartId
    }

    @SagaEventHandler(associationProperty = "cartId")
    fun on(
        event: CartEvents.PurchaseRequired,
        commandGateway: CommandGateway
    ) {
        this.products = event.products.map { SupplyProduct(it) }
        this.products.forEach { product ->
            commandGateway.send<ProductId>(
                SupplyCommands.Buy(
                    product.product.id,
                    product.product.name,
                    product.product.amount
                )
            ).thenApply {
                SagaLifecycle.associateWith("productId", it.id)
            }
        }
    }

    @SagaEventHandler(associationProperty = "productId")
    fun on(
        event: SupplyEvents.Bought,
        commandGateway: CommandGateway,
    ){
        products = products.map {
            if(it.product.id == event.productId){
                it.bought()
            } else {
                it
            }
        }

        if(products.all { it.isBought }){
            commandGateway.send<Unit>(
                CartCommands.SendToCustomer(cartId)
            )
        }
    }

    @SagaEventHandler(associationProperty = "cartId")
    fun on(
        event: CartEvents.Bought,
    ){
        SagaLifecycle.end()
    }

}