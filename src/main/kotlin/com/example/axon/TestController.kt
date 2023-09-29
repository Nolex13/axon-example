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
    @PostMapping("/init")
    fun init() {
        commandGateway.send<ProductId>(
            SellableCommands.Fill(
                id = "PR_01HBG0DW8DPM6ZG6HBNBCNH5N8".toProductId(),
                name = "Keyboard".toProductName(),
                amount = Money.of("10 EUR"),
                quantity = Quantity(5)
            )
        )
        commandGateway.send<ProductId>(
            SellableCommands.Fill(
                id ="PR_01HBG0DWCF279HG44BZMVGEZMC".toProductId(),
                name = "Mouse".toProductName(),
                amount = Money.of("5 EUR"),
                quantity = Quantity(3)
            )
        )
        commandGateway.send<ProductId>(
            SellableCommands.Fill(
                id = "PR_01HBG0DWCYSQ6KHT7J576GSHAZ".toProductId(),
                name = "Monitor".toProductName(),
                amount = Money.of("100 EUR"),
                quantity = Quantity(10)
            )
        )
    }

    @PostMapping("/cart/create")
    fun create(@RequestBody request: CreateRequest): CompletableFuture<CartId> =
        commandGateway.send(
            CartCommands.Create(
                request.user.toUser()
            )
        )

    @PostMapping("/cart/{cartId}/product/{productId}")
    fun addProduct(@PathVariable cartId: CartId, @PathVariable productId: ProductId): CompletableFuture<Unit> =
        commandGateway.send(
            CartCommands.AddProduct(cartId, productId)
        )

    @DeleteMapping("/cart/{cartId}/product/{productId}")
    fun removeProduct(@PathVariable cartId: CartId, @PathVariable productId: ProductId): CompletableFuture<Unit> =
        commandGateway.send(
            CartCommands.RemoveProduct(
                cartId, productId
            )
        )

    @PostMapping("/cart/{cartId}/buy")
    fun buy(@PathVariable cartId: CartId): CompletableFuture<Unit> =
        commandGateway.send(
            CartCommands.Checkout(cartId)
        )

    data class CreateRequest(
        val user: String
    )
}