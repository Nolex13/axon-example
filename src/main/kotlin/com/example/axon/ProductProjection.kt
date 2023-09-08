package com.example.axon

import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("ProductProjection")
class ProductProjection(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    @EventHandler
    fun on(event: Events.ProductAdded) {
        jdbcTemplate.update(
            """
            INSERT INTO Product 
            (productId, name, amount, currency, cartId) 
            VALUES (:productId, :name, :amount, :currency, :cartId)
        """, mapOf(
                "productId" to event.productId.id,
                "name" to event.name.name,
                "amount" to event.amount.amount,
                "currency" to event.amount.currency.currencyCode,
                "cartId" to event.cartId.id,
            )
        )
    }

    @EventHandler
    fun on(event: Events.ProductRemoved) {
        jdbcTemplate.update(
            """
            DELETE FROM Product WHERE productId = :productId
        """, mapOf(
                "productId" to event.productId.id,
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