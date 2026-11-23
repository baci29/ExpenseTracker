package com.radovan.expensetracker.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyUtilsTest {
    @Test
    fun parsesCzechDecimalComma() {
        assertEquals(12550L, parseAmountToCents("125,50"))
    }

    @Test
    fun parsesWholeAmount() {
        assertEquals(8500L, parseAmountToCents("85"))
    }

    @Test
    fun roundsToTwoDecimalPlaces() {
        assertEquals(1235L, parseAmountToCents("12,345"))
    }

    @Test
    fun rejectsZeroNegativeAndInvalidValues() {
        assertNull(parseAmountToCents("0"))
        assertNull(parseAmountToCents("-10"))
        assertNull(parseAmountToCents("abc"))
    }
}
