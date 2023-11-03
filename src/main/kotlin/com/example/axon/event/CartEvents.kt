package com.example.axon.event

import com.example.axon.CartId
import com.example.axon.ProductId
import com.example.axon.Quantity
import com.example.axon.User
import org.axonframework.serialization.Revision

sealed class CartEvents {
    @Revision("1.0")
    data class Created(
        val cartId: CartId,
        val user: User
    )

    @Revision("1.0")
    data class ProductAdded(
        val cartId: CartId,
        val productId: ProductId,
        val quantity: Quantity
    )

    @Revision("1.0")
    data class ProductRemoved(
        val cartId: CartId,
        val productId: ProductId,
        val quantity: Quantity
    )

    @Revision("1.0")
    data class PurchaseRequired(
        val cartId: CartId,
    )

    @Revision("1.0")
    data class Bought(
        val cartId: CartId,
    )
}