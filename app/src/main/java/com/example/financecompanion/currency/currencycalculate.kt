package com.example.financecompanion.currency

import java.util.Locale

object Currencycalculate {
    val rates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "INR" to 83.0,
        "JPY" to 150.0
    )

    val symbols = mapOf(
        "USD" to "$",
        "EUR" to "€",
        "GBP" to "£",
        "INR" to "₹",
        "JPY" to "¥"
    )

    fun format(amount: Double, currencyCode: String): String {
        val symbol = symbols[currencyCode] ?: "$"
        return "$symbol${String.format(Locale.US, "%,.2f", amount)}"
    }
}