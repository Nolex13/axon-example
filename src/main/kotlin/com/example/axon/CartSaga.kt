package com.example.axon

import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Saga
class CartSaga {
    lateinit var cartId: CartId

    private var products: MutableMap<ProductId, Quantity> = mutableMapOf()

    @StartSaga
    @SagaEventHandler(associationProperty = "cartId")
    fun on(
        event: CartEvents.PurchaseRequired,
        jdbcTemplate: NamedParameterJdbcTemplate,
        commandGateway: CommandGateway
    ) {
        this.cartId = event.cartId
        products = jdbcTemplate.retrieveProductInCartFor(event.cartId)

        products.forEach { product ->
            SagaLifecycle.associateWith("productId", product.key.id)
            commandGateway.send<Unit>(SellableCommands.Acquire(
                id = product.key,
                quantity = product.value
            ))
        }
    }

    @SagaEventHandler(associationProperty = "productId")
    fun on(
        event: SellableEvents.Acquired,
        commandGateway: CommandGateway,
    ) {
        products.remove(event.productId)

        if (products.isEmpty()) {
            commandGateway.send<Unit>(
                CartCommands.Finalize(cartId)
            )
            SagaLifecycle.end()
        }
    }

    private fun NamedParameterJdbcTemplate.retrieveProductInCartFor(
        cartId: CartId
    ) = query(
        """
                SELECT productId, quantity FROM Cart
                WHERE cartId = :cartId
            """,
        mapOf("cartId" to cartId.id)
    ) { rs, _ ->
        ProductId(rs.getString("productId")) to Quantity(rs.getInt("quantity"))
    }.toMap().toMutableMap()
}