package com.example.axon.event

import com.example.axon.Money
import com.example.axon.ProductId
import com.example.axon.ProductName
import com.example.axon.Quantity
import org.axonframework.serialization.Revision

sealed class SellableEvents {
    @Revision("1.0")
    data class Filled(
        val productId: ProductId,
        val name: ProductName,
        val amount: Money,
        val quantity: Quantity
    )

    @Revision("1.0")
    data class Depleted(
        val productId: ProductId,
    )

    @Revision("1.0")
    data class Acquired(
        val productId: ProductId,
        val remainingQuantity: Quantity
    )
}