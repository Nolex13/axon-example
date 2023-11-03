package com.example.axon

import com.example.axon.command.SellableCommands
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.web.bind.annotation.PostMapping
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
                id = "PR_01HBG0DWCF279HG44BZMVGEZMC".toProductId(),
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
}