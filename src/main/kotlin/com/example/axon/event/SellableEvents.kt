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
}