package com.example.axon.command

import com.example.axon.PurchaseOrderCode
import com.example.axon.VariationCode
import org.axonframework.modelling.command.TargetAggregateIdentifier

sealed class PurchaseOrderCommands {
    data class Create(
        val purchaseOrderCode: PurchaseOrderCode
    )

    data class CreateVariation(
        @TargetAggregateIdentifier
        val purchaseOrderCode: PurchaseOrderCode,
        val variationCode: VariationCode,
        val type: VariationType,
    )

    data class FixVariation(
        @TargetAggregateIdentifier
        val purchaseOrderCode: PurchaseOrderCode,
        val variationCode: VariationCode,
        val type: VariationType,
    )
}

enum class VariationType {
    PURCHASE,
    CHANGE,
    REFUND,
}