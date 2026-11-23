package com.radovan.expensetracker.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

fun parseAmountToCents(input: String): Long? {
    return try {
        val normalized = input.trim()
            .replace("\u00A0", "")
            .replace(" ", "")
            .replace(',', '.')

        if (normalized.isBlank()) return null

        val amount = BigDecimal(normalized)
        if (amount <= BigDecimal.ZERO) return null

        amount.setScale(2, RoundingMode.HALF_UP)
            .movePointRight(2)
            .longValueExact()
    } catch (_: Exception) {
        null
    }
}

fun formatMoney(amountCents: Long): String {
    val amount = amountCents / 100.0
    return NumberFormat.getCurrencyInstance(Locale("cs", "CZ")).format(amount)
}

fun formatAmountForInput(amountCents: Long): String {
    return BigDecimal.valueOf(amountCents, 2)
        .stripTrailingZeros()
        .toPlainString()
        .replace('.', ',')
}
