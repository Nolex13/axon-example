package com.example.axon.aggregate

import com.example.axon.command.CartCommands.AddProduct
import com.example.axon.command.CartCommands.Checkout
import com.example.axon.command.CartCommands.Create
import com.example.axon.command.CartCommands.Finalize
import com.example.axon.command.CartCommands.RemoveProduct
import com.example.axon.event.CartEvents.Bought
import com.example.axon.event.CartEvents.Created
import com.example.axon.event.CartEvents.ProductAdded
import com.example.axon.event.CartEvents.ProductRemoved
import com.example.axon.event.CartEvents.PurchaseRequired
import com.example.axon.CartId
import com.example.axon.ProductId
import com.example.axon.Quantity
import com.example.axon.Quantity.Companion.ZERO
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class Cart() {
    @AggregateIdentifier
    private lateinit var cartId: CartId

    private var lock = false

    private val products: MutableMap<ProductId, Quantity> = mutableMapOf()

    @CommandHandler
    constructor(
        command: Create,
    ) : this() {
        AggregateLifecycle.apply(
            Created(
                cartId = CartId.new(),
                user = command.user
            ),
        )
    }

    @EventSourcingHandler
    fun on(event: Created) {
        cartId = event.cartId
    }

    @CommandHandler
    fun on(command: AddProduct) {
        check(!lock)
        AggregateLifecycle.apply(
            ProductAdded(
                cartId = command.cartId,
                productId = command.productId,
                quantity =  products.getOrDefault(command.productId, ZERO).increment(),
            )
        )
    }

    @EventSourcingHandler
    fun on(event: ProductAdded) {
        products[event.productId] = event.quantity
    }

    @CommandHandler
    fun on(command: RemoveProduct) {
        check(!lock)

        when (val quantity = products[command.productId]) {
            null, ZERO -> throw ProductNotExistsException(cartId, command.productId)
            else -> {
                AggregateLifecycle.apply(
                    ProductRemoved(
                        cartId = cartId,
                        productId = command.productId,
                        quantity = quantity.decrement()
                    )
                )
            }
        }
    }

    @EventSourcingHandler
    fun on(event: ProductRemoved) {
        products[event.productId] = event.quantity
    }

    @CommandHandler
    fun on(command: Checkout) {
        check(!lock)
        check(products.isNotEmpty())

        AggregateLifecycle.apply(
            PurchaseRequired(command.cartId)
        )
    }

    @EventSourcingHandler
    fun on(event: PurchaseRequired) {
        lock = true
    }

    @CommandHandler
    fun on(command: Finalize){
        AggregateLifecycle.apply(
            Bought(command.cartId)
        )
    }
}

data class ProductNotExistsException(
    val cartId: CartId,
    val productId: ProductId,
) : RuntimeException(
    String.format(
        "Product id %s not found in cart id %s",
        cartId.id,
        productId.id
    )
)