package com.radovan.expensetracker.util

data class FinanceEntry(
    val amountCents: Long,
    val isIncome: Boolean
)

fun calculateBalanceCents(entries: Iterable<FinanceEntry>): Long {
    return entries.sumOf { entry ->
        if (entry.isIncome) entry.amountCents else -entry.amountCents
    }
}

fun calculateBudgetProgress(spentCents: Long, budgetCents: Long): Float {
    if (budgetCents <= 0L) return 0f
    return (spentCents.toDouble() / budgetCents.toDouble())
        .coerceIn(0.0, 1.0)
        .toFloat()
}
