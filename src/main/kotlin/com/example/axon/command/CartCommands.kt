package com.example.axon.command

import com.example.axon.CartId
import com.example.axon.ProductId
import com.example.axon.User
import org.axonframework.modelling.command.TargetAggregateIdentifier

sealed class CartCommands {
    data class Create(
        val user: User
    )

    data class AddProduct(
        @TargetAggregateIdentifier
        val cartId: CartId,
        val productId: ProductId,
    )

    data class RemoveProduct(
        @TargetAggregateIdentifier
        val cartId: CartId,
        val productId: ProductId,
    )

    data class Checkout(
        @TargetAggregateIdentifier
        val cartId: CartId,
    )

    data class Finalize(
        @TargetAggregateIdentifier
        val cartId: CartId,
    )
}