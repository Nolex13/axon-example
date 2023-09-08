package com.example.axon

import com.example.axon.Commands.AddProduct
import com.example.axon.Commands.Buy
import com.example.axon.Commands.Create
import com.example.axon.Commands.RemoveProduct
import com.example.axon.Events.Created
import com.example.axon.Events.ProductAdded
import com.example.axon.Events.ProductRemoved
import com.example.axon.Events.PurchaseRequired
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

private const val MAX_NUMBER_OF_PRODUCTS = 5

@Aggregate
class Cart() {
    @AggregateIdentifier
    private lateinit var cartId: CartId

    private val products: MutableList<Product> = mutableListOf()

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
        AggregateLifecycle.apply(
            ProductAdded(
                cartId = command.cartId,
                productId = ProductId.new(),
                name = command.name,
                amount = command.amount
            )
        )
    }

    @EventSourcingHandler
    fun on(event: ProductAdded) {
        products.add(
            Product(
                id = event.productId,
                name = event.name,
                amount = event.amount,
            )
        )
    }

    @CommandHandler
    fun on(command: RemoveProduct){
        val product = products.firstOrNull{
            it.id == command.productId
        }
        if(product == null){
            throw ProductNotExistsException(cartId, command.productId)
        } else {
            AggregateLifecycle.apply(
                ProductRemoved(
                    cartId = cartId,
                    productId = command.productId
                )
            )
        }
    }

    @EventSourcingHandler
    fun on(event: ProductRemoved){
        products.removeIf {
            it.id == event.productId
        }
    }

    @CommandHandler
    fun on(command: Buy) {
        if (products.count() >= MAX_NUMBER_OF_PRODUCTS) {
            throw TooManyProductsInCartException(products.count())
        } else {
            AggregateLifecycle.apply(
                PurchaseRequired(command.cartId)
            )
        }
    }
}

data class TooManyProductsInCartException(
    val numberOfProducts: Int,
    val maxNumberOfProducts: Int = MAX_NUMBER_OF_PRODUCTS
) : RuntimeException(
    String.format(
        "Max number of products allowed in the cart is %d, you have %d products inside",
        maxNumberOfProducts,
        numberOfProducts
    )
)

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