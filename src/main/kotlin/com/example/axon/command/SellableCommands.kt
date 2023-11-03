package com.example.axon.command

import com.example.axon.Money
import com.example.axon.ProductId
import com.example.axon.ProductName
import com.example.axon.Quantity

sealed class SellableCommands {
    data class Fill(
        val id: ProductId,
        val name: ProductName,
        val amount: Money,
        val quantity: Quantity
    )
}