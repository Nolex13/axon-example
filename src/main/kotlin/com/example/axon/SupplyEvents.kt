package com.example.axon

import org.axonframework.serialization.Revision

sealed class SupplyEvents {
    @Revision("1.0")
    data class Bought(
        val id: ProductId,
        val name: ProductName,
        val amount: Money,
    )
}