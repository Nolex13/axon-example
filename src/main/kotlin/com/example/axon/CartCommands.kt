package com.example.axon

import org.axonframework.modelling.command.TargetAggregateIdentifier

sealed class CartCommands {
    data class Create(
        val user: User
    )

    data class AddProduct(
        @TargetAggregateIdentifier
        val cartId: CartId,
        val name: ProductName,
        val amount: Money,
    )

    data class RemoveProduct(
        @TargetAggregateIdentifier
        val cartId: CartId,
        val productId: ProductId,
    )

    data class Buy(
        @TargetAggregateIdentifier
        val cartId: CartId,
    )
}