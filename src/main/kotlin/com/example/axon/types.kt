package com.example.axon

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.github.f4b6a3.ulid.UlidCreator
import java.math.BigDecimal
import java.util.Currency

data class CartId(
    @JsonValue val id: String
){
    companion object{
        fun new(): CartId =
            CartId("CA_${UlidCreator.getUlid()}")

        @JsonCreator
        @JvmStatic
        fun from(id: String) = id.toCartId()
    }
}

@JvmInline
value class User(
    val value: String
)

@JvmInline
value class ProductId(
    val id: String
){
    companion object{
        fun new(): ProductId =
            ProductId("PR_${UlidCreator.getUlid()}")
    }
}

@JvmInline
value class ProductName(
    val name: String
)

data class Product(
    val id: ProductId,
    val name: ProductName,
    val amount: Money,
)

data class Money(
    val amount: BigDecimal,
    val currency: Currency
){
    companion object{
        fun of(value: String): Money{
            val (amount, currency) = value.split(" ")
            return Money(amount.toBigDecimal(), Currency.getInstance(currency))
        }
    }
}

fun String.toCartId() = CartId(this)
fun String.toUser() = User(this)
fun String.toProductName() = ProductName(this)
