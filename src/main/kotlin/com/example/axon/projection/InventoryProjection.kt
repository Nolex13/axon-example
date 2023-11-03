package com.example.axon.projection

import com.example.axon.event.SellableEvents
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("InventoryProjection")
class InventoryProjection(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    @EventHandler
    fun on(event: SellableEvents.Filled) {
        jdbcTemplate.update(
            """
            INSERT INTO Inventory 
            (productId, name, amount, currency, quantity) 
            VALUES (:productId, :name, :amount, :currency, :quantity)
        """, mapOf(
                "productId" to event.productId.id,
                "name" to event.name.name,
                "amount" to event.amount.amount,
                "currency" to event.amount.currency.currencyCode,
                "quantity" to event.quantity.amount
            )
        )
    }
}