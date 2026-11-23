package com.radovan.expensetracker.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FinanceMathTest {
    @Test
    fun calculatesBalanceFromIncomeAndExpenses() {
        val entries = listOf(
            FinanceEntry(amountCents = 4200000L, isIncome = true),
            FinanceEntry(amountCents = 12550L, isIncome = false),
            FinanceEntry(amountCents = 8500L, isIncome = false)
        )

        assertEquals(4178950L, calculateBalanceCents(entries))
    }

    @Test
    fun limitsBudgetProgressToOneHundredPercent() {
        assertEquals(1f, calculateBudgetProgress(30000L, 20000L), 0.0001f)
    }

    @Test
    fun returnsZeroProgressForInvalidBudget() {
        assertEquals(0f, calculateBudgetProgress(1000L, 0L), 0.0001f)
    }
}
