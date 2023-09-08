package com.example.axon

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
        val name: ProductName,
        val amount: Money,
    )

    @Revision("1.0")
    data class ProductRemoved(
        val cartId: CartId,
        val productId: ProductId,
    )

    @Revision("1.0")
    data class PurchaseRequired(
        val cartId: CartId,
        val products: List<Product>
    )
}