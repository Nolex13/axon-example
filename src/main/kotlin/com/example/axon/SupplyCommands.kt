package com.example.axon

sealed class SupplyCommands {
    data class Buy(
        val id: ProductId,
        val name: ProductName,
        val amount: Money,
    )
}