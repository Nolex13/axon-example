package com.example.axon.command

import com.example.axon.Money
import com.example.axon.ProductId
import com.example.axon.ProductName
import com.example.axon.Quantity
import org.axonframework.modelling.command.TargetAggregateIdentifier

sealed class SellableCommands {
    data class Fill(
        val id: ProductId,
        val name: ProductName,
        val amount: Money,
        val quantity: Quantity
    )

    data class Acquire(
        @TargetAggregateIdentifier
        val id: ProductId,
        val quantity: Quantity
    )
}