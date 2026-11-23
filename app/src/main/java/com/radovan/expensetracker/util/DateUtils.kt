package com.radovan.expensetracker.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun isInCurrentMonth(timestamp: Long, now: Long = System.currentTimeMillis()): Boolean {
    val transactionDate = Calendar.getInstance().apply { timeInMillis = timestamp }
    val currentDate = Calendar.getInstance().apply { timeInMillis = now }
    return transactionDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
        transactionDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)
}

fun formatTransactionDate(timestamp: Long): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale("cs", "CZ")).format(Date(timestamp))
}

fun currentMonthLabel(): String {
    return SimpleDateFormat("LLLL yyyy", Locale("en", "US"))
        .format(Date())
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
}
