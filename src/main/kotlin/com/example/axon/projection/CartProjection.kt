package com.example.axon.projection

import com.example.axon.event.CartEvents
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("CartProjection")
class CartProjection(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    @EventHandler
    fun on(event: CartEvents.ProductAdded) {
        jdbcTemplate.update(
            """
            INSERT INTO Cart 
            (productId, cartId, quantity) 
            VALUES (:productId, :cartId, :quantity)
            ON DUPLICATE KEY UPDATE quantity = :quantity
        """, mapOf(
                "productId" to event.productId.id,
                "cartId" to event.cartId.id,
                "quantity" to event.quantity.amount
            )
        )
    }

    @EventHandler
    fun on(event: CartEvents.ProductRemoved) {
        jdbcTemplate.update(
            """
            UPDATE Cart SET quantity=:quantity WHERE productId = :productId AND cartId=:cartId
        """, mapOf(
                "productId" to event.productId.id,
                "cartId" to event.cartId.id,
                "quantity" to event.quantity.amount,
            )
        )
    }

    @ResetHandler
    fun reset() {
        jdbcTemplate.update(
            """
            TRUNCATE Product
        """, MapSqlParameterSource()
        )
    }
}