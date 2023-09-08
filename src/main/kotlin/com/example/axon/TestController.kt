package com.example.axon

import java.util.concurrent.CompletableFuture
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(
    private val commandGateway: CommandGateway
) {
    @PostMapping("/cart/create")
    fun create(@RequestBody request: CreateRequest): CompletableFuture<CartId> =
        commandGateway.send(
            Commands.Create(
                request.user.toUser()
            )
        )

    @PostMapping("/cart/{cartId}/product")
    fun addProduct(@RequestBody request: AddProductRequest, @PathVariable cartId: CartId): CompletableFuture<CartId> =
        commandGateway.send(
            Commands.AddProduct(
                cartId,
                request.name.toProductName(),
                request.amount
            )
        )

    @DeleteMapping("/cart/{cartId}/product/{productId}")
    fun removeProduct(@PathVariable cartId: CartId, @PathVariable productId: ProductId): CompletableFuture<CartId> =
        commandGateway.send(
            Commands.RemoveProduct(
                cartId, productId
            )
        )

    @PostMapping("/cart/{cartId}/buy")
    fun buy(@PathVariable cartId: CartId): CompletableFuture<CartId> =
        commandGateway.send(
            Commands.Buy(cartId)
        )

    data class CreateRequest(
        val user: String
    )

    data class AddProductRequest(
        val name: String,
        val amount: Money,
    )
}