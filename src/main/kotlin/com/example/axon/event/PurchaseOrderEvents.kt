package com.example.axon.event

import com.example.axon.PurchaseOrderCode
import com.example.axon.VariationCode
import com.example.axon.command.PurchaseOrderCommands.CreateVariation
import com.example.axon.command.VariationType
import org.axonframework.serialization.Revision

sealed class PurchaseOrderEvents {
    @Revision("1.0")
    data class Created(
        val purchaseOrderCode: PurchaseOrderCode
    )

    @Revision("1.0")
    data class VariationCreated(
        val purchaseOrderCode: PurchaseOrderCode,
        val variationCode: VariationCode,
        val type: VariationType,
    )

    @Revision("1.0")
    data class VariationFailed(
        val purchaseOrderCode: PurchaseOrderCode,
        val commandPayload: CreateVariation
    )

    @Revision("1.0")
    data class VariationFixed(
        val purchaseOrderCode: PurchaseOrderCode,
        val variationCode: VariationCode,
        val type: VariationType,
    )
}