package com.example.axon

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.github.f4b6a3.ulid.UlidCreator
import java.math.BigDecimal
import java.util.Currency

data class CartId(
    @JsonValue val id: String
) {
    companion object {
        fun new(): CartId =
            CartId("CA_${UlidCreator.getUlid()}")

        @JsonCreator
        @JvmStatic
        fun from(id: String) = id.toCartId()
    }

    override fun toString(): String {
        return id
    }
}

@JvmInline
value class User(
    val value: String
)

data class ProductId(
    @JsonValue val id: String
) {

    companion object {
        fun new(): ProductId =
            ProductId("PR_${UlidCreator.getUlid()}")

        @JsonCreator
        @JvmStatic
        fun from(id: String) = id.toProductId()
    }

    override fun toString(): String = id
}

@JvmInline
value class ProductName(
    val name: String
)

@JvmInline
value class Quantity(
    val amount: Int
) : Comparable<Quantity> {
    fun increment() = Quantity(amount + 1)
    fun decrement() = Quantity(amount - 1)

    companion object {
        val ZERO = Quantity(0)
        val ONE = Quantity(1)
    }

    override fun compareTo(other: Quantity): Int =
        amount.compareTo(other.amount)

    operator fun minus(other: Quantity): Quantity =
        Quantity(this.amount - other.amount)
}

data class Product(
    val id: ProductId,
    val name: ProductName,
    val amount: Money,
)

data class Money(
    val amount: BigDecimal,
    val currency: Currency
) {
    companion object {
        fun of(value: String): Money {
            val (amount, currency) = value.split(" ")
            return Money(amount.toBigDecimal(), Currency.getInstance(currency))
        }
    }
}

fun String.toCartId() = CartId(this)
fun String.toUser() = User(this)
fun String.toProductName() = ProductName(this)
fun String.toProductId() = ProductId(this)
